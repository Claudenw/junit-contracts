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
 * Example of ContractSuite for C interface using the CTImpl
 * 
 */
@RunWith(ContractSuite.class)
@ContractImpl(CImpl.class)
public class CTSuite {
	private IProducer<C> producer = new IProducer<C>() {
		@Override
		public C newInstance() {
			Listener.add("CTSuite.producer.newInstance()");
			return new CImpl();
		}

		@Override
		public void cleanUp() {
			Listener.add("CTSuite.producer.cleanUp()");
		}
	};

	@Contract.Inject
	public IProducer<C> getProducer() {
		return producer;
	}

	@BeforeClass
	public static void beforeClass() {
		Listener.clear();
	}

	@AfterClass
	public static void afterClass() {
		String[] expected = { "CTSuite.producer.newInstance()", "cname",
				"CTSuite.producer.cleanUp()", "CTSuite.producer.newInstance()",
				"cname version of aname", "CTSuite.producer.cleanUp()",
				"CTSuite.producer.newInstance()", "cname version of bname",
				"CTSuite.producer.cleanUp()" };

		List<String> l = Listener.get();
		Assert.assertEquals(l, Arrays.asList(expected));

	}
}
