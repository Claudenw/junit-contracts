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
import org.xenei.junit.contract.ContractTestRunner;
import org.xenei.junit.contract.IProducer;

/**
 * Show that CT executes correctly as a concrete implementation.
 * 
 * This will only run the tests defined in CT without running any other
 * interface tests. Compare this to CImplContractTest
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 */
@RunWith(ContractTestRunner.class)
public class CImplTest extends CT<CImpl> {

	/**
	 * Constructor.
	 */
	public CImplTest() {
		setProducer(new IProducer<CImpl>() {

			@Override
			public CImpl newInstance() {
				Listener.add("CImplTest.producer.newInstance()");
				return new CImpl();
			}

			@Override
			public void cleanUp() {
				Listener.add("CImplTest.producer.cleanUp()");
			}

		});
	}

	/**
	 * Clear the listener for tests.
	 */
	@BeforeClass
	public static void beforeClass() {
		Listener.clear();
	}

	/**
	 * Verify that the listener saw all the expected events.
	 */
	@AfterClass
	public static void afterClass() {
		String[] expected = { "CImplTest.producer.newInstance()", "cname",
				"CImplTest.producer.cleanUp()" };

		List<String> l = Listener.get();
		Assert.assertEquals(l, Arrays.asList(expected));

	}
}
