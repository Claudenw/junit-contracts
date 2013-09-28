/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xenei.junit.contract;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.RunnerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that runs the Contract annotated tests.
 * 
 * Used with <code>@RunWith( ContractSuite.class )</code> this class scans the
 * classes on the class path to find all the test implementations that should be
 * run by this test suite.
 * <p>
 * It must extend a test annotated with (or derived from a super class that is
 * annotated with) <code>@Contract</code> and that implementation must include a
 * method with the <code>@Contract.Inject</code> annotation that returns an
 * instance of the Producer interface that will create an instance of the class
 * under test.
 * 
 */
public class ContractSuite extends ParentRunner<Runner> {
	private static final Logger LOG = LoggerFactory
			.getLogger(ContractSuite.class);
	private final List<Runner> fRunners;

	/*
	 * arguments for the classCode 1 - contractInfo.getPackageName(), 2 -
	 * klass.getSimpleName(), 3 - contractInfo.getSimpleContractName() 4 -
	 * contractInfo.getTestName() 5 - contractInfo.getContractName() 6 -
	 * testInfo.getMethod().getName());
	 */
	private static final String CLASS_CODE = "package %1$s;%n"
			+ "import org.junit.After;%n"
			+ "public class _wrapped_%2$s_%3$s extends %4$s {%n"
			+ "   private IProducer<%5$s> p;%n"
			+ "   public _wrapped_%2$s_%3$s(IProducer<%5$s> p){this.p=p;}%n"
			+ "   public IProducer<%5$s> %6$s(){%n  return p;%n }%n"
			+ "   @After public final void cleanup_wrapped_%2$s_%3$s() {p.cleanUp();}%n"
			+ "}";

	/**
	 * Called reflectively on classes annotated with
	 * <code>@RunWith(Suite.class)</code>
	 * 
	 * @param klass
	 *            the root class
	 * @param builder
	 *            builds runners for classes in the suite
	 * @throws Throwable
	 */
	public ContractSuite(Class<?> klass, RunnerBuilder builder)
			throws Throwable {
		super(klass);

		// find all the contract annotated tests on the class path.
		ContractTestMap contractTestMap = populateAnnotatedClassContainers();

		/*
		 * we have to build concrete classes for abstract classes that implement
		 * the tests we want to add to this suite. To do that we use the
		 * compiler and build classes in a temp directory that we add to the
		 * class path for this run.
		 */
		Path tempDir = null;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager manager = compiler.getStandardFileManager(null,
				null, null);
		Class<?> workingClass = klass;
		try {
			// we have a RunWith annotated class: Klass
			// see if it is in the annotatedClasses
			ContractTestInfo testInfo = contractTestMap
					.getInfoByTestClass(workingClass);
			if (testInfo == null) {
				// we use this class to pick up any extra tests that we want to
				// run
				Class<?> parentClass = workingClass.getSuperclass();

				// klass is not the annotated class so try to find it from a
				// super class
				workingClass = workingClass.getSuperclass();
				while (testInfo == null && workingClass != Object.class) {
					testInfo = contractTestMap.getInfoByTestClass(workingClass);
					workingClass = workingClass.getSuperclass();
				}
				if (testInfo == null) {
					// if we got here the class hierarchy is wrong.
					throw new IllegalArgumentException(
							"Classes annotated as @RunWith( ContractSuite ) ["
									+ klass
									+ "] must be derived from an @Contract annotated class");
				}
				// create the test info with the parent class.
				testInfo = contractTestMap
						.createTestInfo(testInfo, parentClass);
			}
			// this is the instance object that we will use to get the producer
			// instance
			Object baseObj = klass.newInstance();
			// this is the list of all the JUnit runners in the suite.
			List<Runner> r = new ArrayList<Runner>();

			// get all the annotated classes that test interfaces that klass
			// implements.
			// and iterate over them
			for (ContractTestInfo cti : contractTestMap
					.getAnnotatedClasses(testInfo)) {
				// if the test is abstract create a concrete version
				if (cti.isAbstract()) {

					// configure the compiler if necessary
					if (tempDir == null) {
						tempDir = Files.createTempDirectory("JUC_");
						manager.setLocation(StandardLocation.CLASS_OUTPUT,
								Arrays.asList(new File[] { tempDir.toFile() }));
						addPath(tempDir);
					}
					// compile the class and load it with the class loader
					Class<?> wrapperClass = wrapClass(klass, cti, compiler,
							manager, testInfo);
					// add it to the test suite.
					r.add(new ContractTestRunner(wrapperClass, cti.getMethod()
							.getDeclaringClass(), baseObj, testInfo.getMethod()));
				} else {
					// concrete classes are just executed a per normal.
					r.add(builder.runnerForClass(cti.getTestClass()));
				}
			}
			// this is our list of runners.
			fRunners = Collections.unmodifiableList(r);

		} finally {
			// close the manager if we used it.
			if (manager != null) {
				manager.close();
			}
		}

	}

	/**
	 * Creates a concrete implementation of the abstract class for testing.
	 * Concrete classes have the name _wrapped_[baseTestName]_[addedTestName]
	 * 
	 * @param klass
	 *            The class to enclose the new class.
	 * @param contractInfo
	 *            The ContractTestInfo for the class to add to the test
	 * @param compiler
	 *            The compiler to use
	 * @param manager
	 *            The Java file manager to write compiled code to.
	 * @param testInfo
	 *            The ContractTestInfo for the base class class in the test
	 * @return The Compiled class.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class<?> wrapClass(Class<?> klass,
			ContractTestInfo contractInfo, JavaCompiler compiler,
			StandardJavaFileManager manager, ContractTestInfo testInfo)
			throws ClassNotFoundException, IOException {

		String fqName = String.format("%s._wrapped_%s_%s", klass.getPackage()
				.getName(), klass.getSimpleName(), contractInfo
				.getSimpleContractName());
		String source = String.format(CLASS_CODE,
				contractInfo.getPackageName(), klass.getSimpleName(),
				contractInfo.getSimpleContractName(),
				contractInfo.getTestName(), contractInfo.getContractName(),
				testInfo.getMethod().getName());

		JavaSourceFromString[] compList = { new JavaSourceFromString(fqName,
				source) };

		DiagnosticCollector listener = new DiagnosticCollector();
		CompilationTask task = compiler.getTask(null, manager, listener, null,
				null, Arrays.asList(compList));
		Boolean result = task.call();
		// log any errors if they occured
		if (!result) {
			for (Object d : listener.getDiagnostics()) {
				LOG.warn(d.toString());
				// uncomment this if logging is disabled System.out.println(
				// d.toString() );
			}
		}
		return result ? Class.forName(fqName) : null;

	}

	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner child, RunNotifier notifier) {
		child.run(notifier);
	}

	/**
	 * Populate the ContractTestMap with all the contract tests on the
	 * classpath.
	 * 
	 * @return contractTestClasses ContractTestInfo objects for classes
	 *         annotated with @Contract
	 */
	private static ContractTestMap populateAnnotatedClassContainers() {
		ContractTestMap retval = new ContractTestMap();
		// get all the classes that are Contract tests

		for (Class<?> clazz : ClassPathUtils.getClasses("")) {
			// contract annotation is on the test class
			// value of contract annotation is class under test
			Contract c = clazz.getAnnotation(Contract.class);
			if (c != null) {
				retval.add(new ContractTestInfo(clazz, c));
			}
		}
		return retval;
	}

	// add path to class loader
	private static void addPath(Path s) throws Exception {
		File f = s.toFile();
		f.mkdir();
		URL u = f.toURI().toURL();
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL",
				new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { u });
	}

	/**
	 * A file object used to represent source coming from a string.
	 */
	public static class JavaSourceFromString extends SimpleJavaFileObject {
		/**
		 * The source code of this "file".
		 */
		final String code;

		/**
		 * Constructs a new JavaSourceFromString.
		 * 
		 * @param name
		 *            the name of the compilation unit represented by this file
		 *            object
		 * @param code
		 *            the source code for the compilation unit represented by
		 *            this file object
		 */
		JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

	/**
	 * A map like object that maintains information about test classes and the
	 * classes they test.
	 * 
	 */
	private static class ContractTestMap {
		// the map of test classes to the ContractTestInfo for it.
		private Map<Class<?>, ContractTestInfo> classToInfoMap = new HashMap<Class<?>, ContractTestInfo>();
		// the map of classes under test to the ContractTestInfo for it.
		private Map<Class<?>, ContractTestInfo> testToInfoMap = new HashMap<Class<?>, ContractTestInfo>();

		/**
		 * Add a ContractTestInfo to the map.
		 * 
		 * @param info
		 *            the info to add
		 */
		public void add(ContractTestInfo info) {
			classToInfoMap.put(info.getTestClass(), info);
			testToInfoMap.put(info.getContractClass(), info);
		}

		/**
		 * Get a ContractTestInfo for the test class.
		 * 
		 * @param testClass
		 *            The test class.
		 * @return THe ContractTestInfo for the test class.
		 */
		public ContractTestInfo getInfoByTestClass(Class<?> testClass) {
			return classToInfoMap.get(testClass);
		}

		/**
		 * Get a ContractTestInfo for a class under test.
		 * 
		 * @param contract
		 *            The class (interface) under tes.t
		 * @return The ContractTestInfo for the contract class.
		 */
		public ContractTestInfo getInfoByContractClass(Class<?> contract) {
			return testToInfoMap.get(contract);
		}

		/**
		 * Create a new ContractTestInfo from an existing base instance and a
		 * new test class.
		 * 
		 * @param base
		 *            the base ContractTestInfo
		 * @param testClass
		 *            The new testClass
		 * @return A new ContractTestInfo for the testClass.
		 */
		public ContractTestInfo createTestInfo(ContractTestInfo base,
				Class<?> testClass) {
			ContractTestInfo retval = new ContractTestInfo(base, testClass);
			add(retval);
			return retval;
		}

		/**
		 * 
		 * @param cti
		 *            A ContractTestInfo object that represents the test class
		 *            to run.
		 * @return the set of ContractTestInfo objects that represent the
		 *         complete suite of contract tests for the cti object.
		 */
		public Set<ContractTestInfo> getAnnotatedClasses(ContractTestInfo cti) {

			// list of test classes
			Set<ContractTestInfo> testClasses = new HashSet<ContractTestInfo>();
			testClasses.add(cti);

			// list of implementation classes
			Set<Class<?>> implClasses = new HashSet<Class<?>>();
			implClasses.addAll(Arrays.asList(cti.getContractClass()
					.getClasses()));
			implClasses.addAll(Arrays.asList(cti.getContractClass()
					.getInterfaces()));
			Iterator<Class<?>> iter = implClasses.iterator();
			while (iter.hasNext()) {
				ContractTestInfo testInfo = getInfoByContractClass(iter.next());
				if (testInfo != null) {
					testClasses.add(testInfo);
				}
			}
			return testClasses;
		}

	}

	/**
	 * Class that contains the contract test and the class that is the contract
	 * as well as the method used to get the producer implementation for the
	 * tests.
	 * 
	 */
	private static class ContractTestInfo {
		// the test class
		private Class<?> contractTest;
		// the class under test
		private Class<?> contractClass;
		// the method to retrieve the producer implementation
		private Method method;

		/**
		 * Constructor
		 * 
		 * @param contractTest
		 *            The contract under test.
		 * @param c
		 *            The Contract annotation for he contractTest
		 */
		public ContractTestInfo(Class<?> contractTest, Contract c) {
			this.contractTest = contractTest;
			this.contractClass = c.value();

			// find the source injected value
			for (Method m : contractTest.getDeclaredMethods()) {
				if (m.getAnnotation(Contract.Inject.class) != null) {
					if (!m.getReturnType().equals(Void.TYPE)
							&& Modifier.isAbstract(m.getModifiers())) {
						method = m;
						break;
					}
				}
			}
			if (method == null) {
				throw new IllegalStateException(
						"Classes annotated with @Contract ("
								+ contractTest
								+ ") must include a @Contract.Inject annotation on an abstract declared getter method");

			}
		}

		/**
		 * Create a contract test from an existing base instance and a new test
		 * class.
		 * 
		 * @param base
		 *            The existing instance
		 * @param contractTest
		 *            The new test class.
		 */
		public ContractTestInfo(ContractTestInfo base, Class<?> contractTest) {
			this.contractTest = contractTest;
			this.contractClass = base.contractClass;

			Class<?> workingClass = contractTest;
			while (this.method == null && workingClass != Object.class) {
				try {
					this.method = workingClass.getDeclaredMethod(base.method
							.getName());
				} catch (NoSuchMethodException e) {
					// do nothing
				}
				if (this.method == null) {
					workingClass = workingClass.getSuperclass();
				}
			}
			if (this.method == null) {
				throw new IllegalStateException(
						"Classes annotated with @Contract ("
								+ contractTest
								+ ") must include a @Contract.Inject annotation on an abstract declared getter method");
			}
		}

		public String getPackageName() {
			return contractClass.getPackage().getName();
		}

		public String getSimpleContractName() {
			return contractClass.getSimpleName();
		}

		public String getSimpleTestName() {
			return contractTest.getSimpleName();
		}

		public String getContractName() {
			return contractClass.getCanonicalName();
		}

		public String getTestName() {
			return contractTest.getCanonicalName();
		}

		public boolean isAbstract() {
			return Modifier.isAbstract(contractTest.getModifiers());
		}

		public Class<?> getTestClass() {
			return contractTest;
		}

		public Class<?> getContractClass() {
			return contractClass;
		}

		public Method getMethod() {
			return method;
		}
	}
}