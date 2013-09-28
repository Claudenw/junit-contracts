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

import java.lang.reflect.Method;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * Class to run the Contract annotated tests in a suite.
 * 
 */
public class ContractTestRunner extends BlockJUnit4ClassRunner {

	// the setter class that the setter method is in
	private Class<?> setterClass;
	// the instance of the getter object
	private Object getterObj;
	// the getter method to call.
	private Method getter;

	/**
	 * Create a test runner
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
	public ContractTestRunner(Class<?> wrapper, Class<?> setterClass,
			Object getterObj, Method getter) throws InitializationError {
		super(wrapper);
		this.setterClass = setterClass;
		this.getterObj = getterObj;
		this.getter = getter;
	}

	/**
	 * Create the concrete class passing it the producer instance from the
	 * getter class.
	 */
	@Override
	protected Object createTest() throws Exception {
		return getTestClass().getOnlyConstructor().newInstance(
				getter.invoke(getterObj));
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
		return setterClass.getName();
	}

	@Override
	protected Description describeChild(FrameworkMethod method) {
		return Description.createTestDescription(setterClass, testName(method),
				method.getAnnotations());
	}

}
