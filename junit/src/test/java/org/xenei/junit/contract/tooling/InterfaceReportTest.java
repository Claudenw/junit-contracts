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
import org.xenei.log4j.recording.RecordingAppender;
import org.xenei.log4j.recording.events.StringEvent;
import org.xenei.log4j.recording.selector.MessageSelector;
import org.xenei.log4j.recording.selector.Selector;

public class InterfaceReportTest {

	private InterfaceReport interfaceReport;
	
	private String[] packages = { "org.xenei.junit.contract" };
	
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
		assertTrue( names.contains( "org.xenei.junit.contract.CImpl3"));
		assertTrue( names.contains( "org.xenei.junit.contract.EImpl"));
		assertTrue( names.contains( "org.xenei.junit.contract.FImpl"));
		
		
		classes = interfaceReport.getUntestedInterfaces();
		names = getClassNames( classes );
		assertEquals( 3, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.DTImplSuite$ForceA"));
		assertTrue( names.contains( "org.xenei.junit.contract.DTImplSuite$ForceB"));
		assertTrue( names.contains( "org.xenei.junit.contract.F"));
		
		List<Throwable> errors = interfaceReport.getErrors();
		assertEquals( 0, errors.size() );
		
	}
	
	@Test
	public void testSkipClass() throws MalformedURLException
	{
		String[] skipClasses = { "org.xenei.junit.contract.MissingClass", "org.xenei.junit.contract.CImpl3", "org.xenei.junit.contract.DTImplSuite$ForceA" };
		interfaceReport = new InterfaceReport(packages, skipClasses, classLoader);
		
		Set<Class<?>> classes = interfaceReport.getUnImplementedTests();
		Set<String> names = getClassNames( classes );
		assertEquals( 2, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.EImpl"));
		assertTrue( names.contains( "org.xenei.junit.contract.FImpl"));
		
		
		classes = interfaceReport.getUntestedInterfaces();
		names = getClassNames( classes );
		assertEquals( 2, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.DTImplSuite$ForceB"));
		assertTrue( names.contains( "org.xenei.junit.contract.F"));
		
		List<Throwable> errors = interfaceReport.getErrors();
		assertEquals( 0, errors.size() );
		Selector selector = new MessageSelector();
		StringEvent event = new StringEvent( "Skip class org.xenei.junit.contract.MissingClass was not found");
		appender.assertContains(selector, event );
	}


	@Test
	public void testBadClasses() throws MalformedURLException
	{
		String[] myPackages = { "org.xenei.junit.contract", "org.xenei.junit.bad" };
		String[] skipClasses = { "org.xenei.junit.contract.CImpl3", "org.xenei.junit.contract.DTImplSuite$ForceA" };
		String[] expectedErrors = {
				"java.lang.IllegalStateException: Classes annotated with @Contract (class org.xenei.junit.bad.BadNoInject) must include a @Contract.Inject annotation on a non-abstract declared setter method",
				"java.lang.IllegalStateException: Classes annotated with @Contract (class org.xenei.junit.bad.BadAbstrtact) must not be abstract"		
		};
		interfaceReport = new InterfaceReport(myPackages, skipClasses, classLoader);
		
		Set<Class<?>> classes = interfaceReport.getUnImplementedTests();
		Set<String> names = getClassNames( classes );
		assertEquals( 2, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.EImpl"));
		assertTrue( names.contains( "org.xenei.junit.contract.FImpl"));
		
		
		classes = interfaceReport.getUntestedInterfaces();
		names = getClassNames( classes );
		assertEquals( 2, names.size() );
		assertTrue( names.contains( "org.xenei.junit.contract.DTImplSuite$ForceB"));
		assertTrue( names.contains( "org.xenei.junit.contract.F"));
		
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
