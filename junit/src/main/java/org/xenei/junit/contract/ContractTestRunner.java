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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.xenei.junit.contract.info.DynamicTestInfo;
import org.xenei.junit.contract.info.TestInfo;

/**
 * Class to run the Contract annotated tests in a suite or stand alone
 * 
 * 
 */
public class ContractTestRunner extends BlockJUnit4ClassRunner {

	private final TestInfo parentTestInfo;
	// the setter class that the setter method is in
	private final TestInfo testInfo;
	// the instance of the getter object
	private final Object getterObj;
	// the getter method to call.
	private final Method getter;

	/**
	 * Create a test runner within the ContractTestSuite
	 * 
	 * @param wrapper
	 *            The concerte wrapper on the abstract class that we are
	 *            running.
	 * @param setterClass
	 *            The class that has the setter method.
	 * @param getterObj
	 *            The instance of the class that has the producer interface.
	 * @param getter
	 *            The method on the getterObj that returns the producer
	 *            interface..
	 * @throws InitializationError
	 */
	public ContractTestRunner(Object getterObj, TestInfo parentTestInfo,
			TestInfo testInfo) throws InitializationError {
		super(testInfo.getContractTestClass());
		this.parentTestInfo = parentTestInfo;
		this.testInfo = testInfo;
		this.getterObj = getterObj;
		this.getter = parentTestInfo.getMethod();
	}
	
	/**
	 * Create a test runner for stand alone test
	 * 
	 * @param wrapper
	 *            The concerte wrapper on the abstract class that we are
	 *            running.
	 * @param setterClass
	 *            The class that has the setter method.
	 * @param getterObj
	 *            The instance of the class that has the producer interface.
	 * @param getter
	 *            The method on the getterObj that returns the producer
	 *            interface..
	 * @throws InitializationError
	 */
	public ContractTestRunner(Class<?> testClass) throws InitializationError  {
		super(testClass );
		this.parentTestInfo = null;
		this.testInfo =null;	
		this.getterObj =null;
		this.getter = null;
	}

	/**
	 * Create the concrete class passing it the producer instance from the
	 * getter class.
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InitializationError 
	 */
	@Override
	protected Object createTest() throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InitializationError {
		Object retval = getTestClass().getOnlyConstructor().newInstance();
		if (parentTestInfo != null)
		{
			if (parentTestInfo instanceof DynamicTestInfo) {
				DynamicTestInfo dti = (DynamicTestInfo) parentTestInfo;
	
				Object baseProducer = dti.getDynamicInjector().invoke(getterObj);
				testInfo.getMethod().invoke(retval, dti.getProducer(baseProducer));
			} else {
				testInfo.getMethod().invoke(retval, getter.invoke(getterObj));
			}
		}
		return retval;

	}

	/**
	 * Adds to {@code errors} if the test class has more than one constructor,
	 * or if the constructor takes parameters. Override if a subclass requires
	 * different validation rules.
	 */
	@Override
	protected void validateConstructor(List<Throwable> errors) {
		validateOnlyOneConstructor(errors);
	}

	/**
	 * Returns a name used to describe this Runner
	 */
	@Override
	protected String getName() {
		return testInfo==null?super.getName():testInfo.getContractTestClass().getName();
	}

	@Override
	protected Description describeChild(FrameworkMethod method) {
		if (testInfo == null)
		{
			return super.describeChild( method );
		}
		if (parentTestInfo == null)
		{
			return Description.createTestDescription(testInfo.getContractTestClass(),
					testName(method), method.getAnnotations());
		}
		String name = String.format("%s(%s)", testName(method), testInfo.getContractTestClass().getSimpleName());
		return Description.createTestDescription(parentTestInfo.getContractTestClass(),
				name, method.getAnnotations());
	}

	/**
	 * Returns the methods that run tests. Default implementation returns all
	 * methods annotated with {@code @Test} on this class and superclasses that
	 * are not overridden.
	 */
	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		// this is call during construction. testInfo is not yet available.
		return getTestClass().getAnnotatedMethods(ContractTest.class);
	}

}
