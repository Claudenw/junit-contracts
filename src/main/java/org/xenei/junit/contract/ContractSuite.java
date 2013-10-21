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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xenei.junit.contract.info.DynamicSuiteInfo;
import org.xenei.junit.contract.info.DynamicTestInfo;
import org.xenei.junit.contract.info.SuiteInfo;
import org.xenei.junit.contract.info.TestInfo;

/**
 * Class that runs the Contract annotated tests.
 * 
 * Used with <code>@RunWith( ContractSuite.class )</code> this class scans the
 * classes on the class path to find all the test implementations that should be
 * run by this test suite.
 * <p>
 * Tests annotated with <code>@RunWith( ContractSuite.class )</code> must:
 * <ol>
 * <li>Have a <code>ContractImpl</code> annotation specifying the implementation being
 * tested</li>
 * <li>Include a <code>@Contract.Injeect</code> annotated getter that returns
 * an IProducer<x> where "x" is the class specified in the ContractImpl</li>
 * </ol>
 * <p>
 * The ContractSuite will:
 * <ol>
 * <li>Instantiate the class annotated with <code>@RunWith( ContractSuite.class )</code></li>
 * <li>Find all the Contract tests for the class specified by ContractImpl and add them to the
 * test suite</li>
 * <li>execute all of the tests</li>
 * </ol>
 * </p><p>
 * <b>NOTE:</b>If the class annotated with <code>@RunWith( ContractSuite.class )</code> implements
 * Dynamic the above requirements change.  See Dynamic for more information.
 * </p>
 */
public class ContractSuite extends ParentRunner<Runner> {
	private static final Logger LOG = LoggerFactory
			.getLogger(ContractSuite.class);
	private final List<Runner> fRunners;

	/**
	 * Called reflectively on classes annotated with
	 * <code>@RunWith(Suite.class)</code>
	 * 
	 * @param cls
	 *            the root class
	 * @param builder
	 *            builds runners for classes in the suite
	 * @throws Throwable
	 */
	public ContractSuite(Class<?> cls, RunnerBuilder builder) throws Throwable {
		super(cls);

		List<Throwable> errors = new ArrayList<Throwable>();
		// find all the contract annotated tests on the class path.
		ContractTestMap contractTestMap = populateAnnotatedClassContainers(errors);

		Object baseObj = cls.newInstance();
		List<Runner> r;
		if (baseObj instanceof Dynamic) {
			r = addDynamicClasses(builder, errors, contractTestMap,
					(Dynamic) baseObj);
		} else {
			r = addAnnotatedClasses(cls, builder, errors, contractTestMap,
					baseObj);
		}
		if (!errors.isEmpty()) {
			throw new InitializationError(errors);
		}
		fRunners = Collections.unmodifiableList(r);
	}

	/**
	 * Get the ContractImpl annotation. Logs an error if the annotation is not
	 * found.
	 * 
	 * @param cls
	 *            The class to look on
	 * @param errors
	 *            The list of errors to add to if there is an error
	 * @return ContractImpl or null if not found.
	 */
	private ContractImpl getContractImpl(Class<?> cls, List<Throwable> errors) {
		ContractImpl impl = cls.getAnnotation(ContractImpl.class);
		if (impl == null) {
			errors.add(new IllegalArgumentException(
					"Classes annotated as @RunWith( ContractSuite ) [" + cls
							+ "] must also be annotated with @ContractImpl"));
		}
		return impl;
	}

	/**
	 * Add dynamic classes to the suite.
	 * 
	 * @param builder
	 *            The builder to use
	 * @param errors
	 *            The list of errors
	 * @param contractTestMap
	 *            The ContractTest map.
	 * @param dynamic
	 *            The instance of the dynamic test.
	 * @return The list of runners.
	 * @throws InitializationError
	 */
	private List<Runner> addDynamicClasses(RunnerBuilder builder,
			List<Throwable> errors, ContractTestMap contractTestMap,
			Dynamic dynamic) throws InitializationError {
		Class<? extends Dynamic> cls = dynamic.getClass();
		// this is the list of all the JUnit runners in the suite.
		List<Runner> r = new ArrayList<Runner>();
		ContractImpl impl = getContractImpl(cls, errors);
		if (impl == null) {
			return r;
		}
		DynamicSuiteInfo dynamicSuiteInfo = null;
		try {
			dynamicSuiteInfo = new DynamicSuiteInfo(cls, impl);
		} catch (IllegalArgumentException e) {
			errors.add(e);
			return r;
		}
		
		Collection<Class<?>> tests = dynamic.getSuiteClasses();
		if (tests == null || tests.size() == 0)
		{
			errors.add( new IllegalStateException("Dynamic suite did not return a list of classes to execute"));
		}
		else
		{
			for (Class<?> test : tests) {
				RunWith runwith = test.getAnnotation(RunWith.class);
				if (runwith != null && runwith.value().equals(ContractSuite.class)) {
					impl = getContractImpl(test, errors);
					if (impl != null) {
						try {
							DynamicTestInfo parentTestInfo = new DynamicTestInfo(
									test, impl, dynamicSuiteInfo);
							addSpecifiedClasses(r, test, builder, errors,
									contractTestMap, dynamic, parentTestInfo);
						} catch (IllegalStateException e) {
							errors.add(e);
						}
					}
				} else {
					try {
						r.add(builder.runnerForClass(test));
					} catch (Throwable t) {
						errors.add(t);
					}
				}
			}
		}
		return r;

	}

	/**
	 * Add annotated classes to the test
	 * 
	 * @param cls
	 *            the base test class
	 * @param builder
	 *            The builder to use
	 * @param errors
	 *            the list of errors
	 * @param contractTestMap
	 *            The ContractTest map.
	 * @param baseObj
	 *            this is the instance object that we will use to get the
	 *            producer instance.
	 * @return the list of runners
	 * @throws InitializationError
	 */
	private List<Runner> addAnnotatedClasses(Class<?> cls,
			RunnerBuilder builder, List<Throwable> errors,
			ContractTestMap contractTestMap, Object baseObj)
			throws InitializationError {
		List<Runner> r = new ArrayList<Runner>();
		ContractImpl impl = getContractImpl( cls, errors );
		if (impl != null) {
			TestInfo testInfo = contractTestMap
					.getInfoByTestClass(impl.value());
			if (testInfo == null) {
				testInfo = new SuiteInfo(cls, impl);
				contractTestMap.add(testInfo);
			}
			addSpecifiedClasses(r, cls, builder, errors, contractTestMap,
					baseObj, testInfo);
		}
		return r;
	}

	/**
	 * Adds the specified classes to to the test suite.
	 * 
	 * @param r
	 *            The list of runners to add the test to
	 * @param cls
	 *            The class under test
	 * @param builder
	 *            The builder to user
	 * @param errors
	 *            The list of errors.
	 * @param contractTestMap
	 *            The ContractTestMap
	 * @param baseObj
	 * @param parentTestInfo
	 * @throws InitializationError
	 */
	private void addSpecifiedClasses(List<Runner> r, Class<?> cls,
			RunnerBuilder builder, List<Throwable> errors,
			ContractTestMap contractTestMap, Object baseObj,
			TestInfo parentTestInfo) throws InitializationError {
		// this is the list of all the JUnit runners in the suite.

		Set<TestInfo> testClasses = new LinkedHashSet<TestInfo>();
		// we have a RunWith annotated class: Klass
		// see if it is in the annotatedClasses

		BaseClassRunner bcr = new BaseClassRunner(cls);
		if (bcr.computeTestMethods().size() > 0) {
			r.add(bcr);
		}

		// get all the annotated classes that test interfaces that cls
		// implements.
		// and iterate over them
		for (TestInfo testInfo : contractTestMap.getAnnotatedClasses(
				testClasses, parentTestInfo.getContractClass())) {
			r.add(new ContractTestRunner(baseObj, parentTestInfo, testInfo));
		}
		if (r.size() == 0) {
			errors.add( new IllegalArgumentException ("No tests for " + cls));
		}

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
	 * Will add to list of errors for tests that do not have proper annotations.
	 * 
	 * @param errors
	 *            A list of errors.
	 * @return contractTestClasses TestInfo objects for classes annotated with @Contract
	 */
	private static ContractTestMap populateAnnotatedClassContainers(
			List<Throwable> errors) {

		ContractTestMap retval = new ContractTestMap();
		// get all the classes that are Contract tests

		for (Class<?> clazz : ClassPathUtils.getClasses("")) {
			// contract annotation is on the test class
			// value of contract annotation is class under test
			Contract c = clazz.getAnnotation(Contract.class);
			if (c != null) {
				try {
					retval.add(new TestInfo(clazz, c));
				} catch (IllegalStateException e) {
					errors.add(e);
				}
			}
		}
		return retval;
	}

	/**
	 * A map like object that maintains information about test classes and the
	 * classes they test.
	 * 
	 */
	private static class ContractTestMap {
		// the map of test classes to the TestInfo for it.
		private Map<Class<?>, TestInfo> classToInfoMap = new HashMap<Class<?>, TestInfo>();
		// the map of classes under test to the TestInfo for it.
		private Map<Class<?>, TestInfo> testToInfoMap = new HashMap<Class<?>, TestInfo>();

		/**
		 * Add a TestInfo to the map.
		 * 
		 * @param info
		 *            the info to add
		 */
		public void add(TestInfo info) {
			classToInfoMap.put(info.getTestClass(), info);
			testToInfoMap.put(info.getContractClass(), info);
		}

		/**
		 * Get a TestInfo for the test class.
		 * 
		 * @param testClass
		 *            The test class.
		 * @return THe TestInfo for the test class.
		 */
		public TestInfo getInfoByTestClass(Class<?> testClass) {
			return classToInfoMap.get(testClass);
		}

		/**
		 * Get a TestInfo for a class under test.
		 * 
		 * @param contract
		 *            The class (interface) under tes.t
		 * @return The TestInfo for the contract class.
		 */
		public TestInfo getInfoByContractClass(Class<?> contract) {
			return testToInfoMap.get(contract);
		}

		private void getAllInterfaces(Set<Class<?>> set, Class<?> c) {
			if (c == null || c == Object.class) {
				return;
			}
			for (Class<?> i : c.getClasses()) {
				if (i.isInterface()) {
					if (!set.contains(i)) {
						set.add(i);
						getAllInterfaces(set, i);
					}
				}
			}
			for (Class<?> i : c.getInterfaces()) {
				if (!set.contains(i)) {
					set.add(i);
					getAllInterfaces(set, i);
				}
			}
			getAllInterfaces(set, c.getSuperclass());
		}

		/**
		 * 
		 * @param cti
		 *            A TestInfo object that represents the test class to run.
		 * @return the set of TestInfo objects that represent the complete suite
		 *         of contract tests for the cti object.
		 */
		public Set<TestInfo> getAnnotatedClasses(Set<TestInfo> testClasses,
				Class<?> contractClass) {

			// list of test classes
			// list of implementation classes
			Set<Class<?>> implClasses = new LinkedHashSet<Class<?>>();
			getAllInterfaces(implClasses, contractClass);

			for (Class<?> clazz : implClasses) {
				LOG.info("Checking " + clazz);
				TestInfo testInfo = getInfoByContractClass(clazz);
				if (testInfo != null) {
					testClasses.add(testInfo);
				}
			}
			return testClasses;
		}

	}

	/**
	 * Class to run tests added to the base test.
	 * 
	 */
	private class BaseClassRunner extends BlockJUnit4ClassRunner {

		public BaseClassRunner(Class<?> cls) throws InitializationError {
			super(cls);
		}

		@Override
		protected Statement withAfterClasses(Statement statement) {
			return statement;
		}

		@Override
		protected Statement withBeforeClasses(Statement statement) {
			return statement;
		}

		@Override
		protected void validateInstanceMethods(List<Throwable> errors) {
			validatePublicVoidNoArgMethods(After.class, false, errors);
			validatePublicVoidNoArgMethods(Before.class, false, errors);
			validateTestMethods(errors);
		}

		@Override
		protected List<FrameworkMethod> computeTestMethods() {
			List<FrameworkMethod> retval = new ArrayList<FrameworkMethod>();
			for (FrameworkMethod mthd : super.getTestClass()
					.getAnnotatedMethods(Test.class)) {
				if (mthd.getMethod().getDeclaringClass()
						.getAnnotation(Contract.class) == null) {
					retval.add(mthd);
				}
			}
			return retval;
		}
	}
}
