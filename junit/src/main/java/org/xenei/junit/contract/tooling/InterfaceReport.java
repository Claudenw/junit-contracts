package org.xenei.junit.contract.tooling;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

	/**
	 * The classloader we are using.
	 */
	private final ClassLoader classLoader;

	private static final Logger LOG = LoggerFactory
			.getLogger(ContractTestMap.class);


	private static final Comparator<Class<?>> CLASS_NAME_COMPARATOR = new Comparator<Class<?>>() {

		@Override
		public int compare(final Class<?> o1, final Class<?> o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	
	private boolean isInterestingInterface(Class<?> clazz)
	{
		return clazz.isInterface() && 
				!clazz.isAnnotation() && 
				null == clazz.getAnnotation(NoContractTest.class) &&
				! skipClasses.contains(clazz);
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

	public InterfaceReport(final String[] packages, final String[] skipClasses, ClassLoader classLoader)
			throws MalformedURLException {

		this.classLoader = classLoader;
		
		if (packages.length == 0) {
			throw new IllegalArgumentException(
					"At least one package must be specified");
		}

		// find all the contract annotated tests on the class path.
				// this includes classes not in the specified packages
				contractTestMap = ContractTestMap.populateInstance(classLoader, packages);

		packageClasses = new HashSet<Class<?>>();
		for (final String p : packages) {
			packageClasses.addAll(ClassPathUtils.getClasses(classLoader, p));
		}
		
		this.skipClasses = new HashSet<Class<?>>();
		if (skipClasses != null)
		{
			for (final String s : skipClasses)
			{
				try {
					this.skipClasses.add(Class.forName(s, false, classLoader));
				} catch (ClassNotFoundException e) {
					LOG.warn( "Skip class {} was not found", s);
				}
			}
		}
		contractImplMap = ContractImplMap.populateInstance(packageClasses);
	}

	/**
	 * Get the set of errors encountered when discovering contract tests.
	 * 
	 */
	public List<Throwable> getErrors() {
		List<Throwable> retval = new ArrayList<Throwable>();
		for (TestInfo testInfo : contractTestMap.listTestInfo())
		{
			retval.addAll( testInfo.getErrors() );
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
			if (info.getTests().isEmpty()) {
				retval.add(info.getName());
			}
		}
		return retval;
	}

	private static Set<Class<?>> getAllInterfacesForClass(
			Map<Class<?>, Set<Class<?>>> map, Class<?> c) {
		Set<Class<?>> retval = new HashSet<Class<?>>();
		if (c == null || c == Object.class) {
			return Collections.emptySet();
		}
		for (Class<?> i : c.getClasses()) {
			if (i.isInterface()) {
				if (!map.containsKey(i)) {
					map.put(i, getAllInterfacesForClass(map, i));
				}
				retval.addAll(map.get(i));
			}
			else {
				retval.addAll(getAllInterfacesForClass(map, i));
			}
		}
		for (Class<?> i : c.getInterfaces()) {
			if (!map.containsKey(i)) {
				map.put(i, getAllInterfacesForClass(map, i));
			}
			retval.addAll(map.get(i));
		}
		if (!map.containsKey(c.getSuperclass())) {
			retval.addAll(getAllInterfacesForClass(map, c.getSuperclass()));
		}
		return retval;
	}

	/**
	 * Search for classes that extend interfaces with contract tests but that
	 * don't have an implementation of the test producer.
	 * 
	 * @return
	 */
	public Set<Class<?>> getUnImplementedTests() {
		final Set<Class<?>> retval = new TreeSet<Class<?>>(
				CLASS_NAME_COMPARATOR);

		for (Class<?> clazz : packageClasses) {
			// only interested in concrete implementations
			if (!(clazz.isInterface() || 
					Modifier.isAbstract(clazz.getModifiers()) || 
					skipClasses.contains(clazz)  )) 
			{

				// we are only interested if there is no contract test for the
				// class and there parent tests
				LOG.debug("checking {} for contract tests", clazz);
				Set<Class<?>> interfaces = ClassPathUtils.getAllInterfaces(clazz);
				Map<Class<?>, InterfaceInfo > interfaceInfo = getInterfaceInfoMap();
				
				interfaces.retainAll( interfaceInfo.keySet());
				
				// interfaces contains only contract test interfaces that clazz implements.
				if (! interfaces.isEmpty())
				{
					// not empty so we are need to verify that we have a test for clazz
					if (!contractImplMap.hasTestFor(clazz))
					{
						retval.add( clazz );
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
	 * @param args the command line arguments.  
	 * @throws ParseException 
	 * @throws MalformedURLException  
	 */
	public static void main(final String[] args) throws ParseException,
			MalformedURLException 
	{
		final CommandLine commands = new BasicParser()
				.parse(getOptions(), args);
		
		if (commands.hasOption("h"))
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "InterfaceReport", getOptions() );
			System.exit(0);
		}

		if (!commands.hasOption("p")) {
			System.out.println( "At least on package must be specified");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "InterfaceReport", getOptions() );
			System.exit(1);
		}

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (commands.hasOption("d")) {
			String[] dirs = commands.getOptionValues("d");
			URL[] urls = null;
			urls = new URL[dirs.length];
			for (int i = 0; i < dirs.length; i++) {
				urls[i] = new File(dirs[i]).toURI().toURL();
			}
			classLoader = new URLClassLoader(urls, classLoader);
		}

		final InterfaceReport ifReport = new InterfaceReport(
				commands.getOptionValues("p"), commands.getOptionValues("s"), classLoader);

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
			for (Throwable t : ifReport.getErrors()) {
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
		retval.addOption("s", "skipInterfaces", true,
				"A list of interfaces that should not have tests.  See also @NoContractTest annotation");
		return retval;
	}

	/**
	 * The information about an interface.
	 * 
	 * tracks the interface class and the classes that test the interface.
	 *
	 */
	private static class InterfaceInfo {
		// the interface class
		private Class<?> name;
		// the tests that apply to the interface
		private Set<Class<?>> tests;

		/**
		 * Constructor
		 */
		private InterfaceInfo(final Class<?> name) {
			this.name = name;
			this.tests = new HashSet<Class<?>>();
		}

		/**
		 * Add a test to the list of tests that apply to the interface.
		 * @param test The test to add.
		 */
		private void add(final Class<?> test) {
			tests.add(test);
		}

		/**
		 * Get the set of tests for the interface.
		 * @return
		 */
		private Set<Class<?>> getTests() {
			return tests;
		}

		/**
		 * Get the interface class
		 * @return The interface class.
		 */
		private Class<?> getName() {
			return name;
		}
	}

	/**
	 * A mapping of contracts implementations to tests
	 * 
	 * contract implementations are classes annotated with <code>@ContaractImpl</code>
	 * 
	 * tests are the the contract tests being tested by the implementation.  Tests are annotated
	 * with <Code>@Contract</code>.
	 *
	 * A test may have more than one implementation.
	 */
	private static class ContractImplMap {
		// the map of the contract tests to their implementations.
		private Map<Class<?>, Set<Class<?>>> forwardMap;
		// the map of an implementation to the contract it tests.
		private Map<Class<?>, Class<?>> reverseMap;

		/**
		 * Constructor.
		 */
		public ContractImplMap() {
			forwardMap = new HashMap<Class<?>, Set<Class<?>>>();
			reverseMap = new HashMap<Class<?>, Class<?>>();
		}

		/**
		 * Create the contract map.
		 * @param classes A list classes annotated with ContractImpl
		 * @return the contract implementation map.
		 */
		public static ContractImplMap populateInstance(
				Collection<Class<?>> classes) {
			ContractImplMap retval = new ContractImplMap();
			for (final Class<?> c : classes) {
				final ContractImpl contractImpl = c
						.getAnnotation(ContractImpl.class);
				if (contractImpl != null) {
					retval.add(c, contractImpl);
				}
			}
			return retval;
		}

		/**
		 * Add a ContractImpl to the map.
		 * @param contractTestImplClass The class annotated with <code>@ContractImpl</code>
		 * @param contractImpl The ContractImpl annotation.
		 */
		private void add(Class<?> contractTestImplClass, ContractImpl contractImpl) {
			Set<Class<?>> set = forwardMap.get(contractImpl.value());
			if (set == null) {
				set = new HashSet<Class<?>>();
				forwardMap.put(contractImpl.value(), set);
			}
			set.add(contractTestImplClass);
			reverseMap.put(contractTestImplClass, contractImpl.value());
		}
		
		/**
		 * Return true if there is a contract test implementation for a specific contract test.
		 * @param contractTestImplClass The class annotated with <code>@ContractImpl</code>
		 * @return
		 */
		public boolean hasTestFor(Class<?> contractTestImplClass)
		{
			return forwardMap.containsKey(contractTestImplClass);
		}
	}

}
