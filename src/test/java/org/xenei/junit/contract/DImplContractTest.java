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

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * Run the DT tests using the contract suite runner.
 * 
 * This will run the tests defined in DT as well as any other interface tests.
 * Compare this to DImplTest.
 * 
 * Since D does not implement any other interfaces this will only run the DT
 * tests.
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 */
@RunWith(ContractSuite.class)
// run as a contract test
@ContractImpl(DImpl.class)
// testing the DImpl class
public class DImplContractTest {
	// the producer
	private IProducer<DImpl> producer = new IProducer<DImpl>() {
		@Override
		public DImpl newInstance() {
			Listener.add("DImplContractTest.producer.newInstance()");
			return new DImpl();
		}

		@Override
		public void cleanUp() {
			Listener.add("DImplContractTest.producer.cleanUp()");
		}
	};

	/**
	 * The method that will create the producer to be injected into the tests.
	 */
	@Contract.Inject
	public IProducer<DImpl> getProducer() {
		return producer;
	}

	@BeforeClass
	public static void beforeClass() {
		Listener.clear();
	}

	@AfterClass
	public static void afterClass() {
		String[] expected = { "DImplContractTest.producer.newInstance()",
				"dname", "DImplContractTest.producer.cleanUp()",
				"DImplContractTest.producer.newInstance()", "AImpl",
				"DImplContractTest.producer.cleanUp()",
				"DImplContractTest.producer.newInstance()", "BImpl",
				"DImplContractTest.producer.cleanUp()" };

		List<String> l = Listener.get();
		Assert.assertEquals(l, Arrays.asList(expected));

	}
}
