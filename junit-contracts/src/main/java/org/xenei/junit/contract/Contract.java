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
 * Annotation to declare a test is the contract test for an interface.
 * <p>
 * For example <code><pre>
 * 
 * @Contract( A.class ) public class AT {...} </pre></code> Declares
 *            <code>AT</code> as a contract test for <code>A</code>
 * 
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
	 * By default the method must producer an IProducer with the same type as
	 * the ContractImpl.
	 * 
	 * instance of the Producer interface (@see IProducer} that will generate
	 * and instance the object under test.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Inject {
		/**
		 * The class name that will be returned. This is a string that names the
		 * class that should be created for the return type. The default is
		 * "org.xenei.junit.contract.IProducer&lt;%s&gt;"
		 * 
		 * The %s is replaced with the value of the @ContractImpl annotation.
		 * 
		 * @return a string representing the class that will be returned.
		 * 
		 */
		String value() default "IProducer<%s>";
	}
}
