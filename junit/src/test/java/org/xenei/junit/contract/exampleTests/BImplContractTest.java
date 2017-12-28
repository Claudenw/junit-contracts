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

package org.xenei.junit.contract.exampleTests;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

/**
 * Run the BT tests using the contract suite runner.
 * 
 * This will run the tests defined in BT as well as any other interface tests.
 * Compare this to BImplTest.
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 */
// run as a contract test
@RunWith(ContractSuite.class)
// testing the BImpl class.
@ContractImpl(BImpl.class)
public class BImplContractTest {

	/**
	 * the producer we will inject into contract tests
	 */
	private IProducer<BImpl> producer = new IProducer<BImpl>() {

		@Override
		public BImpl newInstance() {
			Listener.add("BImplContractTest.producer.newInstance()");
			return new BImpl();
		}

		@Override
		public void cleanUp() {
			Listener.add("BImplContractTest.producer.cleanUp()");
		}
	};

	/**
	 * Constructor
	 */
	public BImplContractTest() {
	}

	/**
	 * The method to get the producer and inject it in the tests
	 * 
	 * @return Our producer to inject into contract tests.
	 */
	@Contract.Inject
	public IProducer<BImpl> getProducer() {
		return producer;
	}

	/**
	 * reset the listener for the test.
	 */
	@BeforeClass
	public static void beforeClass() {
		Listener.clear();
	}

	/**
	 * Ensure that the listener detected all the events.
	 */
	@AfterClass
	public static void afterClass() {
		String[] expected = { "BImplContractTest.producer.newInstance()",
				"bname", "BImplContractTest.producer.cleanUp()",
				"BImplContractTest.producer.newInstance()",
				"BInt=1", "BImplContractTest.producer.cleanUp()" };

		List<String> l = Listener.get();
		Assert.assertEquals(l, Arrays.asList(expected));

	}

}
