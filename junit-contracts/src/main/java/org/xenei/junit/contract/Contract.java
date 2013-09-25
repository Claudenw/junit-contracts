package org.xenei.junit.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention( RetentionPolicy.RUNTIME )
public @interface Contract {
	Class<?> value();
	
	/**
   * The <code>Contracts</code> annotation specifies the classes to be run when a class
   * annotated with <code>@RunWith(ContractSuite.class)</code> is run.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Inject {
  }
}
