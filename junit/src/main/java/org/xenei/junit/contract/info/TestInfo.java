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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.model.TestClass;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.MethodUtils;

/**
 * Class that contains the contract test and the class that is the contract as
 * well as the method used to get the producer implementation for the tests.
 *
 */
public class TestInfo implements Comparable<TestInfo> {
    // the test class
    private final Class<?> contractTest;
    // the class under test
    private final Class<?> classUnderTest;
    // the list of tests to skip
    private final Class<?>[] skipTests;
    // the list of errors.
    private final List<Throwable> errors;
    // the hash code
    private final int hash;

    // the method to retrieve the producer implementation
    private final Method method;

    /**
     * Constructor
     *
     * @param testSuite
     *            The contract test this is part of
     * @param impl
     *            The class under test.
     * @param m
     *            The method to retrieve the producer
     */
    protected TestInfo(final Class<?> testSuite, final ContractImpl impl, final Method m) {
        this.contractTest = testSuite;
        this.classUnderTest = impl.value();
        this.skipTests = impl.skip();
        this.method = m;
        this.hash = toString().hashCode();
        this.errors = new ArrayList<Throwable>();
        if ((!classUnderTest.isInterface()) && Modifier.isAbstract( classUnderTest.getModifiers() )) {
            errors.add( new IllegalStateException(
                    "Classes annotated with @Contract (" + classUnderTest + ") must not be abstract" ) );
        }
    }

    /**
     * Add an error to the error list.
     * 
     * @param t
     *            The error to add.
     */
    public void addError(final Throwable t) {
        errors.add( t );
    }

    /**
     * Constructor
     *
     * @param contractTest
     *            The contract under test.
     * @param c
     *            The Contract annotation for the contractTest
     */
    public TestInfo(final Class<?> contractTest, final Contract c) {
        this.contractTest = contractTest;
        this.classUnderTest = c.value();
        this.skipTests = new Class<?>[0];
        this.method = MethodUtils.findAnnotatedSetter( contractTest, Contract.Inject.class );
        this.hash = toString().hashCode();
        this.errors = new ArrayList<Throwable>();
        if (Modifier.isAbstract( contractTest.getModifiers() )) {
            errors.add( new IllegalStateException(
                    "Classes annotated with @Contract (" + contractTest + ") must not be abstract" ) );
        }
        if (method == null) {
            errors.add( new IllegalStateException( "Classes annotated with @Contract (" + contractTest
                    + ") must include a @Contract.Inject annotation on a public non-abstract declared setter method" ) );
        }
    }

    /**
     * Get the list of exceptions from the constructor.
     *
     * @return the list of errors from the constructor.
     */
    public List<Throwable> getErrors() {
        return errors;
    }

    /**
     * Determine if there are errors from this TestInfo constructor.
     *
     * @return true if there are errors from the constructor.
     */
    public boolean hasErrors() {
        return errors.size() > 0;
    }

    /**
     * Get an array of classes to skip.
     * 
     * @return The classes that should not be tested.
     */
    public Class<?>[] getSkipTests() {
        return skipTests;
    }

    /**
     * Get the package name of the class under test..
     *
     * @return The contract class package name.
     */
    public String getClassUnderTestPackageName() {
        return classUnderTest.getPackage().getName();
    }

    /**
     * Get the simple name of the class under test.
     *
     * @return Contract class simple name.
     */
    public String getSimpleClassUnderTestName() {
        return classUnderTest.getSimpleName();
    }

    /**
     * Get the contract test simple name.
     *
     * @return the contract test simple name.
     */
    public String getSimpleTestName() {
        return contractTest.getSimpleName();
    }

    /**
     * Get the canonical name of the class under test.
     *
     * @return the contract class canonical name.
     */
    public String getContractName() {
        return classUnderTest.getCanonicalName();
    }

    /**
     * Get the contract test canonical name.
     *
     * @return The contract test canonical name.
     */
    public String getTestName() {
        return contractTest.getCanonicalName();
    }

    /**
     * Return true if the contract test is abstract.
     *
     * @return True if the contract test is abstract.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract( contractTest.getModifiers() );
    }

    /**
     * Get the contract test class.
     *
     * @return The contract test class.
     */
    public Class<?> getContractTestClass() {
        return contractTest;
    }

    /**
     * Get the TestClass for the contract test.
     *
     * @return The TestClass for the contract test.
     */
    public TestClass getJunitTestClass() {
        return new TestClass( contractTest );
    }

    /**
     * Get class under test.
     *
     * @return The contract class.
     */
    public Class<?> getClassUnderTest() {
        return classUnderTest;
    }

    /**
     * Get the method to retrieve the producer implementation.
     *
     * @return The method that retrieves the producer implementation.
     */
    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return String.format( "[%s testing %s]", getSimpleTestName(), getSimpleClassUnderTestName() );
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof TestInfo) {
            return compareTo( (TestInfo) o ) == 0;
        }
        return false;
    }

    @Override
    public int compareTo(final TestInfo o) {
        return toString().compareTo( o.toString() );
    }
}