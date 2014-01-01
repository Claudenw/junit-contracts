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

import org.junit.After;

/**
 * An example Contract test for C interface.
 * 
 * Defining CT as a generic class with the type extending the type we are
 * testing (e.g. CT&lt;T extends C&gt;) ensures that there are no issues with
 * using derived classes in the tests.
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 */
// Define this as the contract test for the C interface
@Contract(C.class)
public class CT<T extends C> {

	private IProducer<T> producer;

	/**
	 * The method used to inject the producer into the test.
	 */
	@Contract.Inject
	public final void setProducer(IProducer<T> producer) {
		this.producer = producer;
	}

	protected final IProducer<T> getProducer() {
		return producer;
	}

	@After
	public final void cleanupCT() {
		producer.cleanUp();
	}

	@ContractTest
	public void testGetCName() {
		Listener.add(getProducer().newInstance().getCName());
	}

}
