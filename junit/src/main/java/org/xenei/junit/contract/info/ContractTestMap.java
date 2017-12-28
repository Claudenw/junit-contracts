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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xenei.classpathutils.ClassPathFilter;
import org.xenei.classpathutils.filter.NameClassFilter;
import org.xenei.classpathutils.filter.NotClassFilter;
import org.xenei.classpathutils.filter.AndClassFilter;
import org.xenei.junit.contract.Contract;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;

/**
 * A map like object that maintains information about test classes and the
 * classes they test.
 *
 */
public class ContractTestMap {
	// the map of test classes to the TestInfo for it.
	private final Map<Class<?>, TestInfo> classToInfoMap;
	// the map of interface under test to the TestInfo for it.
	private final Map<Class<?>, Set<TestInfo>> interfaceToInfoMap;
	// classes we are going to remove from all processing.
	private final ClassPathFilter skipFilter;

	private static final Log LOG = LogFactory.getLog(ContractTestMap.class);

	/**
	 * Constructor
	 * 
	 */
	public ContractTestMap() {
		this(ClassPathFilter.FALSE);
	}

	/**
	 * Constructor
	 * 
	 * @param ignoreFilter
	 *            A filter describing things to ignore
	 */
	public ContractTestMap(ClassPathFilter ignoreFilter) {
		// the map of test classes to the TestInfo for it.
		classToInfoMap = new HashMap<Class<?>, TestInfo>();
		// the map of interface under test to the TestInfo for it.
		interfaceToInfoMap = new HashMap<Class<?>, Set<TestInfo>>();

		final String prop = System.getProperty("contracts.skipClasses");
		if (prop != null) {
			ClassPathFilter cf = null;
			List<String> names = new ArrayList<String>();
			for (final String iFace : prop.split(",")) {
				names.add(iFace.trim());
			}
			cf = new NameClassFilter(names);

			skipFilter = new AndClassFilter(new NotClassFilter(cf), new NotClassFilter(ignoreFilter)).optimize();
		} else {
			skipFilter = new NotClassFilter(ignoreFilter).optimize();
		}

		FastClasspathScanner scanner = new FastClasspathScanner();

		ClassAnnotationMatchProcessor mp = new ClassAnnotationMatchProcessor() {

			@Override
			public void processMatch(Class<?> matchingClass) {
				if (skipFilter.accept(matchingClass)) {
					final Contract c = matchingClass.getAnnotation(Contract.class);
					LOG.debug(String.format("adding %s %s", matchingClass, c));
					add(new TestInfo(matchingClass, c));
				}
			}
		};

		scanner.matchClassesWithAnnotation(Contract.class, mp);
		scanner.scan();
	}

	/**
	 * Add a TestInfo to the map.
	 *
	 * @param info
	 *            the info to add
	 */
	public void add(final TestInfo info) {
		classToInfoMap.put(info.getContractTestClass(), info);
		Set<TestInfo> tiSet = interfaceToInfoMap.get(info.getClassUnderTest());
		if (tiSet == null) {
			tiSet = new HashSet<TestInfo>();
			interfaceToInfoMap.put(info.getClassUnderTest(), tiSet);
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
			LOG.debug(String.format("Found no tests for interface %s.", contract));
			return Collections.emptySet();
		}
		return interfaceToInfoMap.get(contract);
	}

	/**
	 * Get all interfaces the class implements.
	 * 
	 * @param clazz
	 *            The class to check
	 * @return The set of interfaces.
	 */
	public Set<Class<?>> getAllInterfaces(Class<?> clazz) {
		Set<Class<?>> result = new HashSet<Class<?>>();
		getAllInterfaces(result, clazz);
		return result;
	}

	private void getAllInterfaces(Set<Class<?>> set, Class<?>... clazz) {
		if (clazz != null) {

			for (Class<?> c : clazz) {
				if (c != null) {
					if (c.isInterface() && !set.contains(c)) {
						set.add(c);
					}
					getAllInterfaces(set, c.getInterfaces());
					getAllInterfaces(set, c.getSuperclass());
				}
			}
		}
	}

	/**
	 * Find all the test classes that test the interfaces that contractClassInfo
	 * implements
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
	public Set<TestInfo> getAnnotatedClasses(final Set<TestInfo> testClasses, final TestInfo contractClassInfo) {

		Collection<Class<?>> lst = getAllInterfaces(contractClassInfo.getClassUnderTest());
		for (Class<?> clazz : lst) {
			testClasses.addAll(getInfoByInterfaceClass(clazz));
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