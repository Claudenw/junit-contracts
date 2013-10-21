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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Run the C tests using the contract suite runner on CImpl2. This test includes
 * calling the extra method on CImpl2.
 * 
 * This will run the tests defined in CT as well as AT (A contract tests) and BT
 * (B contract tests). Compare this to CImplTest.
 * 
 * Note that producer used for the AT and BT classes will be the
 * IProducer&lt;CImpl2$gt; from this class.
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 */
@RunWith(ContractSuite.class)
// run as a contract test
@ContractImpl(CImpl2.class)
// testing the CImpl2 class.
public class CImpl2ContractTest {
	// the producer to use for all the tests
	private IProducer<CImpl2> producer = new IProducer<CImpl2>() {
		@Override
		public CImpl2 newInstance() {
			Listener.add("CImpl2ContractTest.producer.newInstance()");
			return new CImpl2();
		}

		@Override
		public void cleanUp() {
			Listener.add("CImpl2ContractTest.producer.cleanUp()");
		}
	};

	/**
	 * Test to test the extra method of the CImpl2 class not defined by the C
	 * interface.
	 */
	@Test
	public void testExtraMethod() {
		Listener.add(producer.newInstance().extraMethod());
	}

	/**
	 * Method to cleanup after the above test.
	 */
	@After
	public final void afterCImpl2ContractTest() {
		producer.cleanUp();
	}

	/**
	 * The method to inject the producer into the test classes.
	 */
	@Contract.Inject
	public IProducer<CImpl2> getProducer() {
		return producer;
	}

	@BeforeClass
	public static void beforeClass() {
		Listener.clear();
	}

	@AfterClass
	public static void afterClass() {
		String[] expected = { "CImpl2ContractTest.producer.newInstance()",
				"called Extra Method", "CImpl2ContractTest.producer.cleanUp()",
				"CImpl2ContractTest.producer.newInstance()", "cname",
				"CImpl2ContractTest.producer.cleanUp()",
				"CImpl2ContractTest.producer.newInstance()",
				"cname version of aname",
				"CImpl2ContractTest.producer.cleanUp()",
				"CImpl2ContractTest.producer.newInstance()",
				"cname version of bname",
				"CImpl2ContractTest.producer.cleanUp()" };

		List<String> l = Listener.get();
		Assert.assertEquals(l, Arrays.asList(expected));

	}
}
