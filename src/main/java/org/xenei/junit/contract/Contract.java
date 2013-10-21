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

import org.junit.Ignore;

/**
 * Annotation to declare a test is the contract test for an interface.
 * <p>
 * For example <code><pre>
 * 
 * @Contract( Foo.class ) public class FooT {...} </pre></code> Declares
 *            <code>FooT</code> as a contract test for <code>Foo</code>
 * 
 * When using this annotation it is recommended to add the @Ignore annotation
 * as well to prevent some test runners from attempting to run the tests 
 * outside of the ContractSuite runner.
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Contract {
	/**
	 * The class that the annotated class is the contract test for.
	 */
	Class<?> value();

	/**
	 * The <code>Contract.Inject</code> annotation specifies the method to be
	 * called to retrieve an instance of a producer for the class under test.
	 * 
	 * The method must produce an instance of IProducer.
	 * 
	 * instance of the Producer interface (@see IProducer} that will generate
	 * and instance the object under test.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Inject {
	}
}
