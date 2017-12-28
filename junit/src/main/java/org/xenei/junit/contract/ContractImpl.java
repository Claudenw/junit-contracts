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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to declare the type the ContractSuite is testing.
 * <p>
 * For example
 * 
 * <pre>
 * 
 * &#64;RunWith( ContractSuite.class )
 * &#64;ContractImpl( FooImpl.class ) public class Foo_Test {...}
 * </pre>
 * <p>
 * Declares <code>FooImpl</code> as the implementation that the contract suite
 * should be built for.
 * </p>
 * <p>
 * The value of the annotation (FooImpl.class) in the above example) defines the
 * class that will be scanned for interfaces. The set of contract tests are then
 * scanned looking for tests for those interfaces. Tests from all matching
 * Contract tests are then added to the current test class.
 * </p>
 * <p>
 * <b>NOTE:</b> In some cases only the interface is known, not the
 * implementation. In these cases a testing interface may be created and that
 * class used in the ContractImpl annotation. For example assume that you want
 * to test an interface A. create an interface:
 * <code>interface ATester extends A{};</code> and in the ContractImpl
 * annotation use ATester.class
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContractImpl {
	/**
	 * The Implementation class that should be scanned for interfaces that have
	 * Contract tests defined.
	 * 
	 * @return The class the implementation class.
	 */
	Class<?> value();

	/**
	 * The list of interface classes for which tests should be skipped.
	 * This list are interfaces that the class under tests implements but that should 
	 * not be tested.
	 * 
	 * @return The interfaces to skip testing.
	 */
	Class<?>[] skip() default {};

	/**
	 * The list of implementations to skip. 
	 * This list is a list of test implementations that apply to the class under test
	 * but that should not be executed.  This is often 
	 * because the tests are not applicable to this implementation. 
	 * 
	 * @return the list of interface test implementations to skip.
	 */
	Class<?>[] ignore() default {};
}
