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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.xenei.junit.bad.BadAbstract;
import org.xenei.junit.bad.BadNoInject;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

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
// run as a contract test
@RunWith(ContractSuite.class)
// testing the CImpl2 class.
@ContractImpl(value = CImpl2.class, ignore = { BadNoInject.class })
public class CImpl2ContractTest {
	// the producer to use for all the tests
	private final IProducer<CImpl2> producer = new IProducer<CImpl2>() {
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
	@ContractTest
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
	 * 
	 * @return The producer we are injecting into the test classes.
	 */
	@Contract.Inject
	public IProducer<CImpl2> getProducer() {
		return producer;
	}

	/**
	 * Clear the listener before the test
	 */
	@BeforeClass
	public static void beforeClass() {
		Listener.clear();
	}

	private static void verifyTest( List<String> expectedTests, List<String> results) {
		Assert.assertEquals( "CImpl2ContractTest.producer.newInstance()", results.get(0));
		Assert.assertTrue( expectedTests.contains( results.get(1)));
		expectedTests.remove( results.get(1));
		Assert.assertEquals( "CImpl2ContractTest.producer.cleanUp()", results.get(2));
		
	}
	/**
	 * Verify that the Listener recorded all the expected events.
	 */
	@AfterClass
	public static void afterClass() {
		final String[] testNames = {"called Extra Method","cname", "cname version of aname", "cname version of bname" };
		final List<String> expectedTests = new ArrayList<String>(Arrays.asList(testNames));
		
		
		final List<String> l = Listener.get();
		
		for (int i=0;i<testNames.length;i++)
		{
			int j = i*3;
			verifyTest( expectedTests, l.subList(j, j+3));
		}
		

	}
}
