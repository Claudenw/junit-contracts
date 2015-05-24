/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xenei.junit.contract.info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xenei.junit.contract.ClassPathUtils;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.filter.AndClassFilter;
import org.xenei.junit.contract.filter.ClassFilter;
import org.xenei.junit.contract.filter.HasAnnotationClassFilter;
import org.xenei.junit.contract.filter.PrefixClassFilter;

/**
 * A map like object that maintains information about test classes and the
 * classes they test.
 *
 */
public class ContractTestMap {
	// the map of test classes to the TestInfo for it.
	private final Map<Class<?>, TestInfo> classToInfoMap = new HashMap<Class<?>, TestInfo>();
	// the map of interface under test to the TestInfo for it.
	private final Map<Class<?>, Set<TestInfo>> interfaceToInfoMap = new HashMap<Class<?>, Set<TestInfo>>();

	private final static Set<String> skipInterfaces;

	private static final Logger LOG = LoggerFactory
			.getLogger(ContractTestMap.class);

	static {
		skipInterfaces = new HashSet<String>();
		final String prop = System.getProperty("contracts.skipClasses");
		if (prop != null) {
			for (final String iFace : prop.split(",")) {
				skipInterfaces.add(iFace);
			}
		}
	}

	/**
	 * Populate and return a ContractTestMap with all the contract tests on the
	 * class path.
	 *
	 * Will add to list of errors for tests that do not have proper annotations.
	 *
	 * @param errors
	 *            A list of errors.
	 * @return contractTestClasses TestInfo objects for classes annotated with @Contract
	 */
	public static ContractTestMap populateInstance() {
		return populateInstance(new Class<?>[0]);
	}

	public static ContractTestMap populateInstance(final Class<?>[] ignoreTests) {
		final List<Class<?>> ignored = Arrays.asList(ignoreTests);
		final ContractTestMap retval = new ContractTestMap();
		// get all the classes that are Contract tests

		for (final Class<?> clazz : ClassPathUtils.getClasses("")) {
			final boolean skip = skipInterfaces.contains(clazz.getName())
					|| ignored.contains(clazz);
			if (!skip) {
				// contract annotation is on the test class
				// value of contract annotation is class under test
				LOG.debug("seeking contracts for {}", clazz);
				final Contract c = clazz.getAnnotation(Contract.class);
				final Ignore ignore = clazz.getAnnotation(Ignore.class);
				if ((c != null) && (ignore == null)) {
					LOG.debug("adding {} {}", clazz, c);
					retval.add(new TestInfo(clazz, c));
				}
			}
		}
		return retval;
	}

	/**
	 * Populate and return a ContractTestMap with all the contract tests on the
	 * classpath.
	 *
	 * Will add to list of errors for tests that do not have proper annotations.
	 *
	 * @param classLoader
	 *            The class loader to load classes from.
	 * @return contractTestClasses TestInfo objects for classes annotated with @Contract
	 */
	public static ContractTestMap populateInstance(final ClassLoader classLoader) {
		return populateInstance(classLoader, new String[] {
				""
		});
	}

	/**
	 *
	 * @param classLoader
	 *            The class loader to use.
	 * @param packages
	 *            A list of package names to report
	 * @return A ContractTestMap.
	 */
	public static ContractTestMap populateInstance(
			final ClassLoader classLoader, final String[] packages) {
		return populateInstance( classLoader, new PrefixClassFilter( packages ));
	}

	/**
	 *
	 * @param classLoader
	 *            The class loader to use.
	 * @param packages
	 *            A list of package names to report
	 * @return A ContractTestMap.
	 */
	public static ContractTestMap populateInstance(
			final ClassLoader classLoader, final ClassFilter filter) {
		final ContractTestMap retval = new ContractTestMap();
		// get all the classes that are Contract tests

		for (final Class<?> clazz : ClassPathUtils.getClasses(classLoader, "", filter)) {
			// contract annotation is on the test class
			// value of contract annotation is class under test
			final Contract c = clazz.getAnnotation(Contract.class);
			if (c != null) {
				retval.add(new TestInfo(clazz, c));
			}
		}

		return retval;
	}
	
	/**
	 * Add a TestInfo to the map.
	 *
	 * @param info
	 *            the info to add
	 */
	public void add(final TestInfo info) {

		classToInfoMap.put(info.getContractTestClass(), info);
		Set<TestInfo> tiSet = interfaceToInfoMap.get(info.getInterfaceClass());
		if (tiSet == null) {
			tiSet = new HashSet<TestInfo>();
			interfaceToInfoMap.put(info.getInterfaceClass(), tiSet);
		}
		tiSet.add(info);
	}

	/**
	 * Get a TestInfo for the test class.
	 *
	 * @param testClass
	 *            The test class.
	 * @return THe TestInfo for the test class.
	 */
	public TestInfo getInfoByTestClass(final Class<?> testClass) {
		return classToInfoMap.get(testClass);
	}

	/**
	 * Get a TestInfo for a interface under test.
	 *
	 * Will not return null, may return an empty set
	 *
	 * @param contract
	 *            The class (interface) under test.
	 * @return The set of TestInfo for the contract class.
	 */
	public Set<TestInfo> getInfoByInterfaceClass(final Class<?> contract) {
		final Set<TestInfo> ti = interfaceToInfoMap.get(contract);
		if (ti == null) {
			LOG.info(String
					.format("Found no tests for interface %s.", contract));
			return Collections.emptySet();
		}
		return interfaceToInfoMap.get(contract);
	}

	/**
	 * Find the test classes for the specific contract class.
	 *
	 * @param contractClassInfo
	 *            A TestInfo object that represents the test class to search
	 *            for.
	 * @return the set of TestInfo objects that represent the complete suite of
	 *         contract tests for the contractClassInfo object.
	 */
	public Set<TestInfo> getAnnotatedClasses(final TestInfo contractClassInfo) {
		return getAnnotatedClasses(new LinkedHashSet<TestInfo>(),
				contractClassInfo);

	}

	/**
	 * Find the test classes for the specific contract class.
	 *
	 * Adds the results to the testClasses parameter set.
	 *
	 *
	 * @param testClasses
	 *            A set of testInfo to add the result to.
	 * @param contractClassInfo
	 *            A TestInfo object that represents the test class to search
	 *            for.
	 * @return the set of TestInfo objects that represent the complete suite of
	 *         contract tests for the contractClassInfo object.
	 */
	public Set<TestInfo> getAnnotatedClasses(final Set<TestInfo> testClasses,
			final TestInfo contractClassInfo) {

		// populate the set of implementation classes
		final Set<Class<?>> implClasses = ClassPathUtils
				.getAllInterfaces(contractClassInfo.getInterfaceClass());
		final List<Class<?>> skipList = Arrays.asList(contractClassInfo
				.getSkipTests());
		for (final Class<?> clazz : implClasses) {
			if (skipList.contains(clazz)) {
				LOG.info(String.format("Skipping %s for %s", clazz,
						contractClassInfo));
			}
			else {
				testClasses.addAll(getInfoByInterfaceClass(clazz));
			}
		}
		return testClasses;
	}

	/**
	 * A list of all test Infos.
	 *
	 * @return
	 */
	public Collection<TestInfo> listTestInfo() {
		return classToInfoMap.values();
	}
}