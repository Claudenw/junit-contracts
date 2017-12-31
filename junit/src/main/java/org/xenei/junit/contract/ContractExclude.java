package org.xenei.junit.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to exclude specific test methods from the contract suite. May be
 * used multiple times.
 * <p>
 * For example
 * 
 * <pre>
 * 
 * &#64;RunWith( ContractSuite.class )
 * &#64;ContractImpl( FooImpl.class )
 * &#64;ContractExclude( ExpertFooTests.class, fns="{barTest,bazTest} )
 * public class Foo_Test {...}
 * </pre>
 * <p>
 * Declares that in the <code>ExpertFooTests</code> class the
 * <code>barTest</code>, and <code>bazTest</code> methods should not be
 * executed.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ContractExcludes.class)
public @interface ContractExclude {
    /**
     * The Contract test implementation (annotated with &#64;Contract) that
     * declares the methods that should not be executed.
     * 
     * @return The Contract test class.
     */
    Class<?> value();

    /**
     * The list of interface classes for which tests should be skipped. This
     * list are interfaces that the class under tests implements but that should
     * not be tested.
     * 
     * @return The interfaces to skip testing.
     */
    String[] methods();

}
