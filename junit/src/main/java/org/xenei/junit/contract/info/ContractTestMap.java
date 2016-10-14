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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xenei.junit.contract.ClassPathUtils;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.filter.AndClassFilter;
import org.xenei.junit.contract.filter.ClassFilter;
import org.xenei.junit.contract.filter.HasAnnotationClassFilter;
import org.xenei.junit.contract.filter.NameClassFilter;
import org.xenei.junit.contract.filter.NotClassFilter;
import org.xenei.junit.contract.filter.OrClassFilter;
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

	// classes we are going to remove from all processing.
	private final static ClassFilter SKIP_FILTER;

	private static final Log LOG = LogFactory
			.getLog(ContractTestMap.class);

	static {
		final String prop = System.getProperty("contracts.skipClasses");
		if (prop != null) {
			ClassFilter cf = null;

			for (final String iFace : prop.split(",")) {
				if (cf == null) {
					cf = new NameClassFilter(iFace.trim());
				} else {
					if (cf instanceof NameClassFilter) {
						cf = new OrClassFilter(cf, new NameClassFilter(
								iFace.trim()));
					} else {
						((OrClassFilter) cf)
								.addClassFilter(new NameClassFilter(iFace
										.trim()));
					}
				}
			}
			SKIP_FILTER = cf;
		} else {
			// skip none.
			SKIP_FILTER = ClassFilter.FALSE;
		}
	}

	/**
	 * Populate and return a ContractTestMap with all the contract tests on the
	 * class path.
	 * 
	 * Contract tests must be annotated with @Contract and must not be annotated
	 * with @Ignore
	 * 
	 * Uses the current thread context class loader.
	 *
	 * @return A newly constructed ContractTestMap.
	 */
	public static ContractTestMap populateInstance() {
		return populateInstance(ClassFilter.TRUE, ClassFilter.FALSE);
	}

	// the filters we are going to ignore.
	private static ClassFilter createIgnoreFilter(final ClassFilter ignoreFilter) {
		return new AndClassFilter(new NotClassFilter(SKIP_FILTER),
				new NotClassFilter(ignoreFilter), new HasAnnotationClassFilter(
						Contract.class), new NotClassFilter(
						new HasAnnotationClassFilter(Ignore.class)));
	}

	/**
	 * Populate an instance of ContractTestMap based on the package filter and
	 * the class filter.
	 * 
	 * Contract tests must be annotated with @Contract and must not be annotated
	 * with @Ignore
	 * 
	 * Uses the current thread context class loader.
	 *
	 * @param packageFilter
	 *            The ClassFilter object to filter package names by.
	 * @param ignoreFilter
	 *            The ClassFilter object to specify classes to ignore.
	 * @return A newly constructed ContractTestMap.
	 */
	public static ContractTestMap populateInstance(
			final ClassFilter packageFilter, final ClassFilter ignoreFilter) {

		final ContractTestMap retval = new ContractTestMap();
		// get all the classes that are Contract tests
		ClassFilter filter = new AndClassFilter(packageFilter,
				createIgnoreFilter(ignoreFilter));
		for (final Class<?> clazz : ClassPathUtils.getClasses("", filter)) {
			// contract annotation is on the test class
			// value of contract annotation is class under test
			final Contract c = clazz.getAnnotation(Contract.class);
			LOG.debug(String.format("adding %s %s", clazz, c));
			retval.add(new TestInfo(clazz, c));
		}
		return retval;
	}

	/**
	 * Populate and return a ContractTestMap using the specified class loader.
	 *
	 * Contract tests must be annotated with @Contract and must not be annotated
	 * with @Ignore
	 *
	 * @param classLoader
	 *            The class loader to load classes from.
	 * @return contractTestClasses TestInfo objects for classes annotated with @Contract
	 */
	public static ContractTestMap populateInstance(final ClassLoader classLoader) {
		return populateInstance(classLoader, ClassFilter.FALSE,
				ClassFilter.TRUE);
	}

	/**
	 * Create an instance of ContractTestMap using the specified class loader.
	 * 
	 * Contract tests must be annotated with @Contract and must not be annotated
	 * with @Ignore
	 * 
	 * @param classLoader
	 *            The class loader to use
	 * @param packageFilter
	 *            The ClassFilter object to filter package names by.
	 * @param ignoreFilter
	 *            The ClassFilter object to specify classes to ignore.
	 * @return A newly constructed ContractTestMap.
	 */
	public static ContractTestMap populateInstance(
			final ClassLoader classLoader, final ClassFilter packageFilter,
			final ClassFilter ignoreFilter) {

		ClassFilter filter = new AndClassFilter(packageFilter,
				createIgnoreFilter(ignoreFilter));
		return populateInstance(classLoader, filter);
	}

	/**
	 * Create an instance of ContractTestMap using the specified class loader.
	 * 
	 * Contract tests must be annotated with @Contract and must not be annotated
	 * with @Ignore
	 * 
	 * @param classLoader
	 *            The class loader to use.
	 * @param packages
	 *            A list of package names to report
	 * @return A ContractTestMap.
	 */
	public static ContractTestMap populateInstance(
			final ClassLoader classLoader, final String[] packages) {
		return populateInstance(classLoader, new PrefixClassFilter(packages));
	}

	/**
	 * Create a ContractTestMap with using the specified class loader. Contract
	 * tests must be annotated with @Contract and must not be annotated with @Ignore
	 * 
	 * @param classLoader
	 *            The class loader to use.
	 * @param filter
	 *            The Class filter to filter the classes with.
	 * 
	 * @return A ContractTestMap.
	 */
	public static ContractTestMap populateInstance(
			final ClassLoader classLoader, final ClassFilter filter) {
		final ContractTestMap retval = new ContractTestMap();
		// get all the classes that are Contract tests

		ClassFilter fltr = new AndClassFilter(filter,
				new HasAnnotationClassFilter(Contract.class));

		for (final Class<?> clazz : ClassPathUtils.getClasses(classLoader, "",
				fltr)) {
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
			LOG.debug(String
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
				LOG.debug(String.format("Skipping %s for %s", clazz,
						contractClassInfo));
			} else {
				testClasses.addAll(getInfoByInterfaceClass(clazz));
			}
		}
		return testClasses;
	}

	/**
	 * A list of all TestInfo objects.
	 *
	 * @return the list of all TestInfo objects.
	 */
	public Collection<TestInfo> listTestInfo() {
		return classToInfoMap.values();
	}
}