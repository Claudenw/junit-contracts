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

import java.lang.reflect.Method;

import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.MethodUtils;

/**
 * Class that contains the contract test and the class that is the contract as
 * well as the method used to get the producer implementation for the suite
 * tests.
 *
 */
public class SuiteInfo extends TestInfo {

	/**
	 * Constructor
	 *
	 * @param testSuite
	 *            the test suite definition class.
	 */
	public SuiteInfo(final Class<?> testSuite, final ContractImpl impl) {
		super(testSuite, impl, MethodUtils.findAnnotatedGetter(testSuite,
				Contract.Inject.class));
		if (this.getMethod() == null) {
			addError(new IllegalStateException(
					"Classes annotated with @RunWith(ContractSuite.class) ("
							+ getContractTestClass()
							+ ") must include a @Contract.Inject annotation on a concrete declared getter method"));
		}
	}

	protected SuiteInfo(final Class<?> testSuite, final ContractImpl impl,
			final Method m) {
		super(testSuite, impl, m);
	}
}