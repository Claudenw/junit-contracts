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

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xenei.junit.contract.filter.ClassFilter;
import org.xenei.junit.contract.filter.parser.Parser;
import org.xenei.log4j.recording.RecordingAppender;


public class InterfaceReportTest {

	private InterfaceReport interfaceReport;
	
	private String[] packages = { "org.xenei.junit.contract.exampleTests" };
	
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	
	private static RecordingAppender appender;
	
	private Set<String> getClassNames( Set<Class<?>> classes )
	{
		TreeSet<String> retval = new TreeSet<String>();
		for (Class<?> c : classes )
		{
			retval.add( c.getName () );
		}
		return retval;
	}
	
	@BeforeClass
	public static void beforeClass()
	{
		appender  = new RecordingAppender();
		Logger root = Logger.getRootLogger();
		root.addAppender( appender );
	}
	
	@After
	public void afterTest()
	{
		appender.clear();
	}
	
	@AfterClass
	public static void afterClass()
	{
		Logger root = Logger.getRootLogger();
		root.removeAppender( appender );
		appender.close();
	}
	
	@Test
	public void testPlain() throws MalformedURLException
	{
		interfaceReport = new InterfaceReport(packages,null,classLoader);
		
		Set<Class<?>> classes = interfaceReport.getUnImplementedTests();
		Set<String> names = getClassNames( classes );
		assertEquals( 3, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.CImpl3"));
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.EImpl"));
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.FImpl"));
		
		
		classes = interfaceReport.getUntestedInterfaces();
		names = getClassNames( classes );
		assertEquals( 1, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.F"));
		
		List<Throwable> errors = interfaceReport.getErrors();
		assertEquals( 0, errors.size() );
		
	}
	
	@Test
	public void testSkipClass() throws MalformedURLException
	{
		ClassFilter filter = new Parser().parse( "Not( Or( Name( org.xenei.junit.contract.exampleTests.MissingClass ),Name( org.xenei.junit.contract.exampleTests.CImpl3 ), Name( org.xenei.junit.contract.exampleTests.DTImplSuite$ForceA ) ) )");

		interfaceReport = new InterfaceReport(packages, filter, classLoader);
		
		Set<Class<?>> classes = interfaceReport.getUnImplementedTests();
		Set<String> names = getClassNames( classes );
		assertEquals( 2, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.EImpl"));
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.FImpl"));
		
		
		classes = interfaceReport.getUntestedInterfaces();
		names = getClassNames( classes );
		assertEquals( 1, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.F"));
		
		List<Throwable> errors = interfaceReport.getErrors();
		assertEquals( 0, errors.size() );
	}


	@Test
	public void testBadClasses() throws MalformedURLException
	{
		String[] myPackages = { "org.xenei.junit.contract.exampleTests", "org.xenei.junit.bad" };
		String[] expectedErrors = {
				"java.lang.IllegalStateException: Classes annotated with @Contract (class org.xenei.junit.bad.BadNoInject) must include a @Contract.Inject annotation on a public non-abstract declared setter method",
				"java.lang.IllegalStateException: Classes annotated with @Contract (class org.xenei.junit.bad.BadAbstract) must not be abstract"		
		};
		ClassFilter filter = new Parser().parse( "Not( Or( Name( org.xenei.junit.contract.exampleTests.CImpl3 ), Name( org.xenei.junit.contract.exampleTests.DTImplSuite$ForceA ) ) )");
		interfaceReport = new InterfaceReport(myPackages, filter, classLoader);
		
		Set<Class<?>> classes = interfaceReport.getUnImplementedTests();
		Set<String> names = getClassNames( classes );
		assertEquals( 2, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.EImpl"));
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.FImpl"));
		
		
		classes = interfaceReport.getUntestedInterfaces();
		names = getClassNames( classes );
		assertEquals( 1, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.exampleTests.F"));
		
		List<Throwable> errors = interfaceReport.getErrors();
		assertEquals( 2, errors.size() );
		List<String> errStr = new ArrayList<String>();
		for (Throwable t : errors )
		{
			errStr.add( t.toString() );
		}
		
		for (int i=0;i<expectedErrors.length;i++)
		{
			assertTrue( "missing: "+expectedErrors[i], errStr.contains( expectedErrors[i] ));
		}
			
	}
	
}
