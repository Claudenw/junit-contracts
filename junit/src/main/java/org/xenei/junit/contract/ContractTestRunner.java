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
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
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
    private final List<Method> excludedMethods;

    /**
     * Create a test runner within the ContractTestSuite.
     * 
     * @param getterObj
     *            The object on which we will execute the method that gets the
     *            producer.
     * @param parentTestInfo
     *            The test info for the parent.
     * @param testInfo
     *            The test info for this test.
     * @param excludedMethods
     *            A list of test methods that should not be executed.
     * 
     * @throws InitializationError
     *             on error.
     */
    public ContractTestRunner(Object getterObj, TestInfo parentTestInfo, TestInfo testInfo,
            List<Method> excludedMethods) throws InitializationError {
        super( testInfo.getContractTestClass() );
        this.parentTestInfo = parentTestInfo;
        this.testInfo = testInfo;
        this.getterObj = getterObj;
        this.getter = parentTestInfo.getMethod();
        this.excludedMethods = excludedMethods;
    }

    /**
     * Create a test runner for stand alone test
     * 
     * @param testClass
     *            The ContractTest annotated class.
     * @throws InitializationError
     *             on error.
     */
    public ContractTestRunner(Class<?> testClass) throws InitializationError {
        super( testClass );
        this.parentTestInfo = null;
        this.testInfo = null;
        this.getterObj = null;
        this.getter = null;
        this.excludedMethods = Collections.emptyList();
    }

    /**
     * Create the concrete class passing it the producer instance from the
     * getter class.
     * 
     * @throws InvocationTargetException
     *             if the testClass can not be initialized with
     *             <code>newInstance()</code>
     * @throws IllegalArgumentException
     *             if any of the classes can not be initialized or the dynamic
     *             method can not be called.
     * @throws InvocationTargetException
     *             if any of the classes can not be initialized or the dynamic
     *             method can not be called.
     */
    @Override
    protected Object createTest() throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final Object retval = getTestClass().getOnlyConstructor().newInstance();
        if (parentTestInfo != null) {
            if (parentTestInfo instanceof DynamicTestInfo) {
                final DynamicTestInfo dti = (DynamicTestInfo) parentTestInfo;

                final Object baseProducer = dti.getDynamicInjector().invoke( getterObj );
                testInfo.getMethod().invoke( retval, dti.getProducer( baseProducer ) );
            } else {
                testInfo.getMethod().invoke( retval, getter.invoke( getterObj ) );
            }
        }
        return retval;

    }

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        final Description description = describeChild( method );
        if (method.getAnnotation( Ignore.class ) != null || excludedMethods.contains( method.getMethod() )) {
            notifier.fireTestIgnored( description );
        } else {
            runLeaf( methodBlock( method ), description, notifier );
        }
    }

    /**
     * Adds to {@code errors} if the test class has more than one constructor,
     * or if the constructor takes parameters. Override if a subclass requires
     * different validation rules.
     */
    @Override
    protected void validateConstructor(List<Throwable> errors) {
        validateOnlyOneConstructor( errors );
    }

    /**
     * Returns a name used to describe this Runner
     */
    @Override
    protected String getName() {
        return testInfo == null ? super.getName() : testInfo.getContractTestClass().getName();
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        if (testInfo == null) {
            return super.describeChild( method );
        }
        if (parentTestInfo == null) {
            return Description.createTestDescription( testInfo.getContractTestClass(), testName( method ),
                    method.getAnnotations() );
        }
        final String name = String.format( "%s(%s)", testName( method ),
                testInfo.getContractTestClass().getSimpleName() );
        return Description.createTestDescription( parentTestInfo.getContractTestClass(), name,
                method.getAnnotations() );
    }

    /**
     * Returns the methods that run tests. Default implementation returns all
     * methods annotated with {@code @Test} on this class and superclasses that
     * are not overridden.
     */
    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        // this is call during construction. testInfo and excludedMethods is not
        // yet available.
        return getTestClass().getAnnotatedMethods( ContractTest.class );
    }

    @Override
    public String toString() {
        return "ContractTest" + testInfo == null ? "" : testInfo.toString();
    }

}
