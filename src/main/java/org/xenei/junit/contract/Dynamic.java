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
 *
 */
public interface Dynamic {

	public Collection<Class<?>> getSuiteClasses();

	/**
	 * The <code>Dynamic.Inject</code> annotation specifies the getter to be
	 * called to retrieve an instance of a producer for the suite under test.
	 * 
	 * The classes included in the suite must have a Contract.Inject setter
	 * that accepts the type returned by this method.  That method must then 
	 * set the internals of the test so that the Contract.Inject getter will
	 * produce the proper object for the specific test.
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Inject {
	}
}
