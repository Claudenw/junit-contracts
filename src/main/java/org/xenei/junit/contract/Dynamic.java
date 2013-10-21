package org.xenei.junit.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Interface that defines a test suite as being dynamic.
 * 
 * Dynamic test suites generate the list of test classes after they are instantiated
 * 
 * Dynamic test suites implementations must have a method that is annotated with
 * Dynamic.Inject and specifies the base injector method.
 * <p>
 * Tests that extend Dynamic alter the requirements of the ContractSuite.  The suite must:
 * <ol>
 * <li>Have a <code>ContractImpl</code> annotation specifying the implementation being
 * tested</li>
 * <li>Include a <code>@Dynamic.Inject</code> annotated getter that returns
 * an IProducer<x> where "x" is the class specified in the ContractImpl</li>
 * <li>Each <code>@RunWith(ContractSuite)</code> annotated class listed in getSuiteClasses() must:
 * <ol>
 * <li>Include a <code>@Contract.Inject</code> annotated setter that accepts the producer
 * returned by the Dynamic.Inject annotated method above.</li>
 * <li>Include a <code>@Contract.Inject</code> annotated getter that returns the producer
 * required for the test.</li>
 * </ol>
 * In general these classes convert the <code>@Dynamic.Inject</code> annottated producer into a producer
 * that the underlying test can use.
 * </li>
 * </ol>
 * <p>
 * The ContractSuite will:
 * <ol>
 * <li>Instantiate the class annotated with <code>@RunWith( ContractSuite.class )</code></li>
 * <li>Find all the suites tests for the class specified by getSuiteClasses() and add them to the
 * test suite.  If any of those are annotated with ContractSuite the contract suite actions as
 * described above will be executed.  All other tests should run as expected.</li>
 * <li>execute all of the tests</li>
 * </ol>
 */
public interface Dynamic {

	public Collection<Class<?>> getSuiteClasses();

	/**
	 * The <code>Dynamic.Inject</code> annotation specifies the getter to be
	 * called to retrieve an instance of a producer for the suite under test.
	 * 
	 * The classes included in the suite must have a Contract.Inject setter
	 * that accepts the type returned by this method.  That method must then 
	 * set the internals of the test so that the Contract.Inject annoated getter will
	 * produce the proper object for the specific test.
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Inject {
	}
}
