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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import org.xenei.classpathutils.ClassPathFilter;
import org.xenei.classpathutils.filter.HasAnnotationClassFilter;
import org.xenei.classpathutils.filter.NameClassFilter;
import org.xenei.classpathutils.filter.OrClassFilter;
import org.xenei.junit.contract.info.ContractTestMap;
import org.xenei.junit.contract.info.DynamicSuiteInfo;
import org.xenei.junit.contract.info.DynamicTestInfo;
import org.xenei.junit.contract.info.SuiteInfo;
import org.xenei.junit.contract.info.TestInfo;
import org.xenei.junit.contract.info.TestInfoErrorRunner;

/**
 * Class that runs the Contract annotated tests.
 *
 * Used with <code>&#64;RunWith( ContractSuite.class )</code> this class scans
 * the classes on the class path to find all the test implementations that
 * should be run by this test suite.
 * <p>
 * Tests annotated with <code>&#64;RunWith( ContractSuite.class )</code> must:
 * </p>
 * <ol>
 * <li>Have a <code>ContractImpl</code> annotation specifying the implementation
 * being tested</li>
 * <li>Include a <code>@Contract.Inject</code> annotated getter that returns an
 * IProducer&lt;x&gt; where "x" is the class specified in the ContractImpl</li>
 * </ol>
 * <p>
 * The ContractSuite will:
 * </p>
 * <ol>
 * <li>Instantiate the class annotated with
 * <code>@RunWith( ContractSuite.class )</code></li>
 * <li>Find all the Contract tests for the class specified by ContractImpl and
 * add them to the test suite</li>
 * <li>execute all of the &#64;ContractTest annotated tests</li>
 * </ol>
 * <p>
 * <b>NOTE:</b>If the class annotated with
 * <code>@RunWith( ContractSuite.class )</code> implements Dynamic the above
 * requirements change. See Dynamic for more information.
 * </p>
 */
public class ContractSuite extends ParentRunner<Runner> {
    private static final Log LOG = LogFactory.getLog( ContractSuite.class );
    private final List<Runner> fRunners;

    /**
     * Called reflectively on classes annotated with
     * <code>@RunWith(Suite.class)</code>
     *
     * @param contractTest
     *            the root class
     * @param builder
     *            builds runners for classes in the suite
     * @throws InitializationError
     *             if super class can not be instantiated with contractTest.
     * @throws IllegalAccessException
     *             if the contractTest can not be initialized with
     *             <code>newInstance()</code>
     * @throws InstantiationException
     *             if the contractTest can not be initialized with
     *             <code>newInstance()</code>
     */
    public ContractSuite(final Class<?> contractTest, final RunnerBuilder builder)
            throws InitializationError, InstantiationException, IllegalAccessException {

        super( contractTest );

        final ContractImpl contractImpl = contractTest.getAnnotation( ContractImpl.class );
        // find all the contract annotated tests on the class path.
        ClassPathFilter ignoreFilter = new HasAnnotationClassFilter( Ignore.class );
        if (contractImpl.ignore().length > 0) {

            final List<String> lst = new ArrayList<String>();
            for (final Class<?> c : contractImpl.ignore()) {
                lst.add( c.getName() );
            }
            ignoreFilter = new OrClassFilter( ignoreFilter, new NameClassFilter( lst ) );
        }

        final ContractTestMap contractTestMap = new ContractTestMap( ignoreFilter );
        final TestInfo testInfo = contractTestMap.getInfoByTestClass( contractTest );
        List<Runner> runners;
        if ((testInfo != null) && testInfo.hasErrors()) {
            runners = new ArrayList<Runner>();
            runners.add( new TestInfoErrorRunner( contractTest, testInfo ) );
        } else {
            final Object baseObj = contractTest.newInstance();

            if (baseObj instanceof Dynamic) {
                runners = addDynamicClasses( builder, contractTestMap, (Dynamic) baseObj );
            } else {
                runners = addAnnotatedClasses( contractTest, builder, contractTestMap, baseObj );
            }
        }

        fRunners = Collections.unmodifiableList( runners );
    }

    /**
     * Get the ContractImpl annotation. Logs an error if the annotation is not
     * found.
     *
     * @param cls
     *            The class to look on
     * @return ContractImpl or null if not found.
     * @throws InitializationError
     */
    private ContractImpl getContractImpl(final Class<?> cls) throws InitializationError {
        final ContractImpl impl = cls.getAnnotation( ContractImpl.class );
        if (impl == null) {

            throw new InitializationError( "Classes annotated as @RunWith( ContractSuite ) [" + cls
                    + "] must also be annotated with @ContractImpl" );
        }
        return impl;
    }

    /**
     * Get the ContractExclude annotation and extract the list of methods from
     * it.
     * 
     * logs error if method is not found on the class specified in the
     * annotation.
     *
     * @param cls
     *            The class on which to for the annotation.
     * @param errors
     *            The list of errors to add to if there is an error
     * @return A list of methods. May be empty.
     * @throws InitializationError
     */
    private List<Method> getExcludedMethods(final Class<?> cls) {

        final List<Method> lst = new ArrayList<Method>();
        for (final ContractExclude exclude : cls.getAnnotationsByType( ContractExclude.class )) {
            final Class<?> clazz = exclude.value();
            for (final String mthdName : exclude.methods()) {
                try {
                    lst.add( clazz.getDeclaredMethod( mthdName ) );
                } catch (NoSuchMethodException | SecurityException e) {
                    LOG.warn( String.format( "ContractExclude annotation on %s incorrect", cls ), e );
                }
            }
        }
        return lst;
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
    private List<Runner> addDynamicClasses(final RunnerBuilder builder, final ContractTestMap contractTestMap,
            final Dynamic dynamic) throws InitializationError {
        final Class<? extends Dynamic> dynamicClass = dynamic.getClass();
        // this is the list of all the JUnit runners in the suite.
        final List<Runner> runners = new ArrayList<Runner>();
        ContractImpl impl = getContractImpl( dynamicClass );
        if (impl == null) {
            return runners;
        }
        final DynamicSuiteInfo dynamicSuiteInfo = new DynamicSuiteInfo( dynamicClass, impl );

        final Collection<Class<?>> tests = dynamic.getSuiteClasses();
        if ((tests == null) || (tests.size() == 0)) {
            dynamicSuiteInfo
            .addError( new InitializationError( "Dynamic suite did not return a list of classes to execute" ) );
            runners.add( new TestInfoErrorRunner( dynamicClass, dynamicSuiteInfo ) );
        } else {
            for (final Class<?> test : tests) {
                final RunWith runwith = test.getAnnotation( RunWith.class );
                if ((runwith != null) && runwith.value().equals( ContractSuite.class )) {
                    impl = getContractImpl( test );
                    if (impl != null) {
                        final DynamicTestInfo parentTestInfo = new DynamicTestInfo( test, impl, dynamicSuiteInfo );

                        if (!parentTestInfo.hasErrors()) {
                            addSpecifiedClasses( runners, test, builder, contractTestMap, dynamic, parentTestInfo );
                        }
                        // this is not an else as addSpecifiedClasses may add
                        // errors to parentTestInfo
                        if (parentTestInfo.hasErrors()) {
                            runners.add( new TestInfoErrorRunner( dynamicClass, parentTestInfo ) );
                        }
                    }
                } else {
                    try {
                        runners.add( builder.runnerForClass( test ) );
                    } catch (final Throwable t) {
                        throw new InitializationError( t );
                    }
                }
            }
        }
        return runners;

    }

    /**
     * Add annotated classes to the test
     *
     * @param baseClass
     *            the base test class
     * @param builder
     *            The builder to use
     * @param contractTestMap
     *            The ContractTest map.
     * @param baseObj
     *            this is the instance object that we will use to get the
     *            producer instance.
     * @return the list of runners
     * @throws InitializationError
     */
    private List<Runner> addAnnotatedClasses(final Class<?> baseClass, final RunnerBuilder builder,
            final ContractTestMap contractTestMap, final Object baseObj) throws InitializationError {
        final List<Runner> runners = new ArrayList<Runner>();
        final ContractImpl impl = getContractImpl( baseClass );
        if (impl != null) {
            TestInfo testInfo = contractTestMap.getInfoByTestClass( impl.value() );
            if (testInfo == null) {
                testInfo = new SuiteInfo( baseClass, impl );
                contractTestMap.add( testInfo );
            }

            if (!testInfo.hasErrors()) {
                addSpecifiedClasses( runners, baseClass, builder, contractTestMap, baseObj, testInfo );
            }
            // this is not an else since addSpecifiedClasses may add errors to
            // testInfo.
            if (testInfo.hasErrors()) {
                runners.add( new TestInfoErrorRunner( baseClass, testInfo ) );
            }
        }
        return runners;
    }

    /**
     * Adds the specified classes to to the test suite.
     *
     * May add error notations to the parentTestInfo.
     *
     * @param runners
     *            The list of runners to add the test to
     * @param testClass
     *            The class under test
     * @param builder
     *            The builder to user
     * @param errors
     *            The list of errors.
     * @param contractTestMap
     *            The ContractTestMap
     * @param baseObj
     *            The object under test
     * @param parentTestInfo
     *            The parent test Info.
     * @throws InitializationError
     */
    private void addSpecifiedClasses(final List<Runner> runners, final Class<?> testClass, final RunnerBuilder builder,
            final ContractTestMap contractTestMap, final Object baseObj, final TestInfo parentTestInfo)
                    throws InitializationError {

        // this is the list of all the JUnit runners in the suite.
        final Set<TestInfo> testClasses = new LinkedHashSet<TestInfo>();

        // we have a RunWith annotated class: Klass
        // see if it is in the annotatedClasses
        final BaseClassRunner bcr = new BaseClassRunner( testClass );
        if (bcr.computeTestMethods().size() > 0) {
            runners.add( bcr );
        }

        final List<Method> excludeMethods = getExcludedMethods( getTestClass().getJavaClass() );

        /*
         * get all the annotated classes that test the interfaces that
         * parentTestInfo implements and iterate over them
         */
        for (final TestInfo testInfo : contractTestMap.getAnnotatedClasses( testClasses, parentTestInfo )) {

            if (!Arrays.asList( parentTestInfo.getSkipTests() ).contains( testInfo.getClassUnderTest() )) {
                if (testInfo.getErrors().size() > 0) {

                    final TestInfoErrorRunner runner = new TestInfoErrorRunner( testClass, testInfo );

                    runner.logErrors( LOG );
                    runners.add( runner );
                } else {
                    runners.add( new ContractTestRunner( baseObj, parentTestInfo, testInfo, excludeMethods ) );
                }
            }
        }
        if (runners.size() == 0) {
            LOG.info( "No tests for " + testClass );
        }

    }

    @Override
    protected List<Runner> getChildren() {
        return fRunners;
    }

    @Override
    protected Description describeChild(final Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(final Runner child, final RunNotifier notifier) {
        LOG.debug( String.format( "Running: %s ", child ) );
        child.run( notifier );
    }

    /**
     * Class to run tests added to the base test.
     *
     */
    private class BaseClassRunner extends BlockJUnit4ClassRunner {

        private List<FrameworkMethod> testMethods = null;

        public BaseClassRunner(final Class<?> cls) throws InitializationError {
            super( cls );
        }

        @Override
        protected Statement withAfterClasses(final Statement statement) {
            return statement;
        }

        @Override
        protected Statement withBeforeClasses(final Statement statement) {
            return statement;
        }

        @Override
        protected void validateInstanceMethods(final List<Throwable> errors) {
            validatePublicVoidNoArgMethods( After.class, false, errors );
            validatePublicVoidNoArgMethods( Before.class, false, errors );
            validateTestMethods( errors );
        }

        @Override
        protected List<FrameworkMethod> computeTestMethods() {
            if (testMethods == null) {
                testMethods = new ArrayList<FrameworkMethod>();
                final List<Method> excludeMethods = getExcludedMethods( getTestClass().getJavaClass() );
                for (final FrameworkMethod mthd : super.getTestClass().getAnnotatedMethods( ContractTest.class )) {
                    if (mthd.getMethod().getDeclaringClass().getAnnotation( Contract.class ) == null
                            && !excludeMethods.contains( mthd.getMethod() )) {
                        testMethods.add( mthd );
                    }
                }
            }
            return testMethods;
        }
    }
}
