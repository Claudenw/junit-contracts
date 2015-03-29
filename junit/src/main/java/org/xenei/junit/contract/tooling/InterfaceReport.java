package org.xenei.junit.contract.tooling;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xenei.junit.contract.ClassPathUtils;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.NoContractTest;
import org.xenei.junit.contract.info.ContractTestMap;
import org.xenei.junit.contract.info.TestInfo;

public class InterfaceReport {

	/**
	 * A collection of all classes in the package classes. This includes
	 * interfaces, abstract and concrete implementations.
	 */
	private final Collection<Class<?>> packageClasses;

	private final Collection<Class<?>> skipClasses;

	/**
	 * A map of all interfaces implemented in the packages that have contract
	 * tests to their InterfaceInfo record.
	 */
	private Map<Class<?>, InterfaceInfo> interfaceInfoMap;

	/**
	 * A map of interface to all contract tests for the interface. this includes
	 * classes not in the specified packages.
	 **/
	private final ContractTestMap contractTestMap;

	private final ContractImplMap contractImplMap;

	private static final Logger LOG = LoggerFactory
			.getLogger(ContractTestMap.class);

	private static final Comparator<Class<?>> CLASS_NAME_COMPARATOR = new Comparator<Class<?>>() {

		@Override
		public int compare(final Class<?> o1, final Class<?> o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	private boolean isInterestingInterface(final Class<?> clazz) {
		return clazz.isInterface() && !clazz.isAnnotation()
				&& (null == clazz.getAnnotation(NoContractTest.class))
				&& !skipClasses.contains(clazz);
	}

	public Collection<InterfaceInfo> getInterfaceInfoCollection() {
		return getInterfaceInfoMap().values();
	}

	/**
	 * Get the interface info map.
	 *
	 * All interfaces implemented in the packages that have contract tests.
	 *
	 * @return
	 */
	private Map<Class<?>, InterfaceInfo> getInterfaceInfoMap() {
		if (interfaceInfoMap == null) {
			interfaceInfoMap = new HashMap<Class<?>, InterfaceInfo>();
			for (final Class<?> c : packageClasses) {
				if (isInterestingInterface(c)) {
					if (!interfaceInfoMap.containsKey(c)) {
						interfaceInfoMap.put(c, new InterfaceInfo(c));
					}
				}
				else {
					final Contract contract = c.getAnnotation(Contract.class);
					if (contract != null) {
						InterfaceInfo ii = interfaceInfoMap.get(contract
								.value());
						if (ii == null) {
							ii = new InterfaceInfo(contract.value());
							interfaceInfoMap.put(contract.value(), ii);
						}
						ii.add(c);
					} 
				}
			}
		}
		return interfaceInfoMap;
	}

	public InterfaceReport(final String[] packages, final String[] skipClasses,
			final ClassLoader classLoader) throws MalformedURLException {

		if (packages.length == 0) {
			throw new IllegalArgumentException(
					"At least one package must be specified");
		}

		// find all the contract annotated tests on the class path.
		// this includes classes not in the specified packages
		contractTestMap = ContractTestMap.populateInstance(classLoader,
				packages);

		packageClasses = new HashSet<Class<?>>();
		for (final String p : packages) {
			packageClasses.addAll(ClassPathUtils.getClasses(classLoader, p));
		}

		if (packageClasses.size() == 0) {
			throw new IllegalArgumentException("No classes found in "
					+ packages);
		}

		this.skipClasses = new HashSet<Class<?>>();
		if (skipClasses != null) {
			for (final String s : skipClasses) {
				try {
					this.skipClasses.add(Class.forName(s, false, classLoader));
				} catch (final ClassNotFoundException e) {
					LOG.warn("Skip class {} was not found", s);
				}
			}
		}
		contractImplMap = ContractImplMap.populateInstance(packageClasses);
	}

	public Collection<Class<?>> getPackageClasses() {
		return packageClasses;
	}

	/**
	 * Get the set of errors encountered when discovering contract tests.
	 *
	 */
	public List<Throwable> getErrors() {
		final List<Throwable> retval = new ArrayList<Throwable>();
		for (final TestInfo testInfo : contractTestMap.listTestInfo()) {
			retval.addAll(testInfo.getErrors());
		}
		return retval;
	}

	/**
	 * get a set of interfaces that do not have contract tests defined.
	 *
	 * @return The list of interfaces that don't have contract tests defined.
	 */
	public Set<Class<?>> getUntestedInterfaces() {
		final Set<Class<?>> retval = new TreeSet<Class<?>>(
				CLASS_NAME_COMPARATOR);

		for (final InterfaceInfo info : getInterfaceInfoMap().values()) {
			// no test and has methods
			if (info.getTests().isEmpty() && info.getName().getDeclaredMethods().length > 0) {
				retval.add(info.getName());
			}
		}
		return retval;
	}

	// private static Set<Class<?>> getAllInterfacesForClass(
	// final Map<Class<?>, Set<Class<?>>> map, final Class<?> c) {
	// final Set<Class<?>> retval = new HashSet<Class<?>>();
	// if ((c == null) || (c == Object.class)) {
	// return Collections.emptySet();
	// }
	// for (final Class<?> i : c.getClasses()) {
	// if (i.isInterface()) {
	// if (!map.containsKey(i)) {
	// map.put(i, getAllInterfacesForClass(map, i));
	// }
	// retval.addAll(map.get(i));
	// }
	// else {
	// retval.addAll(getAllInterfacesForClass(map, i));
	// }
	// }
	// for (final Class<?> i : c.getInterfaces()) {
	// if (!map.containsKey(i)) {
	// map.put(i, getAllInterfacesForClass(map, i));
	// }
	// retval.addAll(map.get(i));
	// }
	// if (!map.containsKey(c.getSuperclass())) {
	// retval.addAll(getAllInterfacesForClass(map, c.getSuperclass()));
	// }
	// return retval;
	// }

	/**
	 * Search for classes that extend interfaces with contract tests but that
	 * don't have an implementation of the test producer.
	 *
	 * @return
	 */
	public Set<Class<?>> getUnImplementedTests() {
		final Set<Class<?>> retval = new TreeSet<Class<?>>(
				CLASS_NAME_COMPARATOR);

		for (final Class<?> clazz : packageClasses) {
			// only interested in concrete implementations
			if (!(clazz.isInterface()
					|| Modifier.isAbstract(clazz.getModifiers()) || skipClasses
					.contains(clazz))) {

				// we are only interested if there is no contract test for the
				// class and there parent tests
				LOG.debug("checking {} for contract tests", clazz);
				final Set<Class<?>> interfaces = ClassPathUtils
						.getAllInterfaces(clazz);
				final Map<Class<?>, InterfaceInfo> interfaceInfo = getInterfaceInfoMap();

				interfaces.retainAll(interfaceInfo.keySet());

				// interfaces contains only contract test interfaces that clazz
				// implements.
				if (!interfaces.isEmpty()) {
					// not empty so we are need to verify that we have a test
					// for clazz
					if (!contractImplMap.hasTestFor(clazz)) {
						retval.add(clazz);
					}
				}
			}
		}
		return retval;
	}

	/**
	 * Run the interface report generation.
	 *
	 * use -h argument for help and argument list.
	 *
	 * @param args
	 *            the command line arguments.
	 * @throws ParseException
	 * @throws MalformedURLException
	 */
	public static void main(final String[] args) throws ParseException,
			MalformedURLException {
		final CommandLine commands = new BasicParser()
				.parse(getOptions(), args);

		if (commands.hasOption("h")) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("InterfaceReport", getOptions());
			System.exit(0);
		}

		if (!commands.hasOption("p")) {
			System.out.println("At least on package must be specified");
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("InterfaceReport", getOptions());
			System.exit(1);
		}

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (commands.hasOption("d")) {
			final String[] dirs = commands.getOptionValues("d");
			URL[] urls = null;
			urls = new URL[dirs.length];
			for (int i = 0; i < dirs.length; i++) {
				urls[i] = new File(dirs[i]).toURI().toURL();
			}
			classLoader = new URLClassLoader(urls, classLoader);
		}

		final InterfaceReport ifReport = new InterfaceReport(
				commands.getOptionValues("p"), commands.getOptionValues("s"),
				classLoader);

		if (commands.hasOption("u")) {
			System.out.println("Untested Interfaces");
			for (final Class<?> c : ifReport.getUntestedInterfaces()) {
				System.out.println(c.getCanonicalName());
			}
			System.out.println("End of Report");
		}

		if (commands.hasOption("i")) {
			System.out.println("Missing contract test implementations");
			for (final Class<?> c : ifReport.getUnImplementedTests()) {
				System.out.println(c.getName());
			}
			System.out.println("End of Report");
		}

		if (commands.hasOption("e")) {
			System.out.println("Misconfigured contract test report");
			for (final Throwable t : ifReport.getErrors()) {
				System.out.println(t.toString());
			}
			System.out.println("End of Report");
		}
	}

	// the loptions
	private static Options getOptions() {
		final Options retval = new Options();

		retval.addOption("h", "help", false, "Display this help page");
		retval.addOption("p", "package", true, "Package to be scanned");
		retval.addOption("d", "directory", true,
				"Directory to be scanned for classes");
		retval.addOption("u", "untested", false,
				"Produce untested class report");
		retval.addOption("i", "implementation", false,
				"Produce missing implementation report");
		retval.addOption("e", "errors", false,
				"Produce contract test configuration error report");
		retval.addOption(
				"s",
				"skipInterfaces",
				true,
				"A list of interfaces that should not have tests.  See also @NoContractTest annotation");
		return retval;
	}

	/**
	 * A mapping of contracts implementations to tests
	 *
	 * contract implementations are classes annotated with
	 * <code>@ContaractImpl</code>
	 *
	 * tests are the the contract tests being tested by the implementation.
	 * Tests are annotated with <Code>@Contract</code>.
	 *
	 * A test may have more than one implementation.
	 */
	private static class ContractImplMap {
		// the map of the contract tests to their implementations.
		private final Map<Class<?>, Set<Class<?>>> forwardMap;
		// the map of an implementation to the contract it tests.
		private final Map<Class<?>, Class<?>> reverseMap;

		/**
		 * Constructor.
		 */
		public ContractImplMap() {
			forwardMap = new HashMap<Class<?>, Set<Class<?>>>();
			reverseMap = new HashMap<Class<?>, Class<?>>();
		}

		/**
		 * Create the contract map.
		 *
		 * @param classes
		 *            A list classes annotated with ContractImpl
		 * @return the contract implementation map.
		 */
		public static ContractImplMap populateInstance(
				final Collection<Class<?>> classes) {
			final ContractImplMap retval = new ContractImplMap();
			for (final Class<?> c : classes) {
				final ContractImpl contractImpl = c.getAnnotation(ContractImpl.class);
				if (contractImpl != null) {
					retval.add(c, contractImpl);
				}
			}
			return retval;
		}

		/**
		 * Add a ContractImpl to the map.
		 *
		 * @param contractTestImplClass
		 *            The class annotated with <code>@ContractImpl</code>
		 * @param contractImpl
		 *            The ContractImpl annotation.
		 */
		private void add(final Class<?> contractTestImplClass,
				final ContractImpl contractImpl) {
			Set<Class<?>> set = forwardMap.get(contractImpl.value());
			if (set == null) {
				set = new HashSet<Class<?>>();
				forwardMap.put(contractImpl.value(), set);
			}
			set.add(contractTestImplClass);
			reverseMap.put(contractTestImplClass, contractImpl.value());
		}

		/**
		 * Return true if there is a contract test implementation for a specific
		 * contract test.
		 *
		 * @param contractTestImplClass
		 *            The class annotated with <code>@ContractImpl</code>
		 * @return
		 */
		public boolean hasTestFor(final Class<?> contractTestImplClass) {
			return forwardMap.containsKey(contractTestImplClass);
		}
	}

}
