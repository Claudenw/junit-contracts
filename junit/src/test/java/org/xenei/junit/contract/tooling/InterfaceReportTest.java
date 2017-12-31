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

package org.xenei.junit.contract.tooling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xenei.classpathutils.ClassPathFilter;
import org.xenei.classpathutils.filter.NameClassFilter;
import org.xenei.classpathutils.filter.NotClassFilter;
import org.xenei.classpathutils.filter.OrClassFilter;
import org.xenei.junit.bad.BadAbstract;
import org.xenei.log4j.recording.RecordingAppender;

/**
 * Test the InterfaceReport
 *
 */
public class InterfaceReportTest {

    private InterfaceReport interfaceReport;

    private final String[] packages = { "org.xenei.junit.contract.exampleTests" };

    private final String[] badPackages = { "org.xenei.junit.contract.exampleTests", "org.xenei.junit.bad" };

    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    private static RecordingAppender appender;

    private Set<String> getClassNames(Set<Class<?>> classes) {
        final TreeSet<String> retval = new TreeSet<String>();
        for (final Class<?> c : classes) {
            retval.add( c.getName() );
        }
        return retval;
    }

    /**
     * setup for the test. Create an appending recorder so we can verify
     * logging.
     */
    @BeforeClass
    public static void beforeClass() {
        appender = new RecordingAppender();
        final Logger root = Logger.getRootLogger();
        root.addAppender( appender );
    }

    /**
     * Clear the recording appender after tests.
     */
    @After
    public void afterTest() {
        appender.clear();
    }

    /**
     * Clean up after all test by removing the recording appender.
     */
    @AfterClass
    public static void afterClass() {
        final Logger root = Logger.getRootLogger();
        root.removeAppender( appender );
        appender.close();
    }

    /**
     * Test that all the expected unimplemented tests are found.
     */
    @Test
    public void testGetUnimplementedTests() {
        interfaceReport = new InterfaceReport( packages, null, classLoader );

        final Set<Class<?>> classes = interfaceReport.getUnImplementedTests();
        final Set<String> names = getClassNames( classes );
        assertEquals( 3, names.size() );
        assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.CImpl3" ) );
        assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.EImpl" ) );
        assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.FImpl" ) );
    }

    /**
     * Test that all the expected unimplemented tests are found.
     */
    @Test
    public void testGetUntestedInterfaces() {
        interfaceReport = new InterfaceReport( packages, null, classLoader );

        final Set<Class<?>> classes = interfaceReport.getUntestedInterfaces();
        final Set<String> names = getClassNames( classes );
        assertEquals( 1, names.size() );
        assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.F" ) );
    }

    /**
     * test get errors when not filtered.
     */
    @Test
    public void testGetErrors() {

        final String[] expectedErrors = {
                "java.lang.IllegalStateException: Classes annotated with @Contract (class org.xenei.junit.bad.BadNoInject) must include a @Contract.Inject annotation on a public non-abstract declared setter method",
        "java.lang.IllegalStateException: Classes annotated with @Contract (class org.xenei.junit.bad.BadAbstract) must not be abstract" };

        interfaceReport = new InterfaceReport( packages, null, classLoader );

        final List<String> errors = new ArrayList<String>();
        for (final Throwable t : interfaceReport.getErrors()) {
            errors.add( t.toString() );
        }

        for (final String expectedError : expectedErrors) {
            assertTrue( "missing: " + expectedError, errors.contains( expectedError ) );
        }

        Assert.assertEquals( 2, errors.size() );

    }

    /**
     * Test that unimplemented tests is correct
     * 
     */
    @Test
    public void getUnImplementedTests_Filtered() {

        final ClassPathFilter filter = new NotClassFilter(
                new OrClassFilter( new NameClassFilter( "org.xenei.junit.contract.exampleTests.CImpl3" ),
                        new NameClassFilter( "org.xenei.junit.contract.exampleTests.DTImplSuite$ForceA" ) ) );

        interfaceReport = new InterfaceReport( badPackages, filter, classLoader );

        final Set<Class<?>> classes = interfaceReport.getUnImplementedTests();
        final Set<String> names = getClassNames( classes );
        assertTrue( "Missing org.xenei.junit.contract.exampleTests.EImpl",
                names.contains( "org.xenei.junit.contract.exampleTests.EImpl" ) );
        assertTrue( "Missing org.xenei.junit.contract.exampleTests.FImpl",
                names.contains( "org.xenei.junit.contract.exampleTests.FImpl" ) );
        assertEquals( "Too many classes", 2, names.size() );
    }

    /**
     * Test that untested interfaces is correct
     * 
     */
    @Test
    public void getUntestedInterfacesTest_Filtered() {

        final ClassPathFilter filter = new NotClassFilter(
                new OrClassFilter( new NameClassFilter( "org.xenei.junit.contract.exampleTests.CImpl3" ),
                        new NameClassFilter( "org.xenei.junit.contract.exampleTests.DTImplSuite$ForceA" ) ) );

        interfaceReport = new InterfaceReport( badPackages, filter, classLoader );

        final Set<Class<?>> classes = interfaceReport.getUntestedInterfaces();
        final Set<String> names = getClassNames( classes );
        assertEquals( 1, names.size() );
        assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.F" ) );

    }

    /**
     * Test that error reporting reports bad classes.
     * 
     */
    @Test
    public void testGetErrors_Filtered() {

        final String[] expectedErrors = {
                "java.lang.IllegalStateException: Classes annotated with @Contract (class org.xenei.junit.bad.BadNoInject) must include a @Contract.Inject annotation on a public non-abstract declared setter method", };

        final ClassPathFilter filter = new NotClassFilter(
                new OrClassFilter( new NameClassFilter( "org.xenei.junit.contract.exampleTests.CImpl3" ),
                        new NameClassFilter( "org.xenei.junit.contract.exampleTests.DTImplSuite$ForceA" ),
                        new NameClassFilter( BadAbstract.class.getName() ) ) );

        interfaceReport = new InterfaceReport( badPackages, filter, classLoader );

        final List<String> errors = new ArrayList<String>();
        for (final Throwable t : interfaceReport.getErrors()) {
            errors.add( t.toString() );
        }

        for (final String expectedError : expectedErrors) {
            assertTrue( "missing: " + expectedError, errors.contains( expectedError ) );
        }

        Assert.assertEquals( 2, errors.size() );
    }

}
