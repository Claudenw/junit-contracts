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
import org.junit.Ignore;
import org.junit.Test;

/**
 * An example Contract test for D interface.
 * 
 * Defining DT as a generic class with the type extending the type we are
 * testing (e.g. DT&lt;T extends D&gt;) ensures that there are no issues with
 * using derived classes in the tests.
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 */
@Ignore("Is a contract test definition")
// without "ignore" this some test runners will attempt to run this test.
@Contract(D.class)
// Define this as the contract test for the D interface
public class DT<T extends D> {

	// the producer for the tests
	private IProducer<T> producer;

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

	@Test
	public void testGetDName() {
		Listener.add(getProducer().newInstance().getDName());
	}

	@Test
	public void testGetA() {
		Listener.add(getProducer().newInstance().getA().toString());
	}

	@Test
	public void testGetB() {
		Listener.add(getProducer().newInstance().getB().toString());
	}
}
