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
import org.junit.Test;

/**
 * concrete example of CT implementation
 * 
 */
public class CTImpl extends CT {

	private IProducer<C> producer = new IProducer<C>() {
		@Override
		public C newInstance() {
			System.out.println("CTImpl.producer: newInstance was called");
			return new C() {
				@Override
				public String getCName() {
					return "cname";
				}

				@Override
				public String getAName() {
					return "cname version of aname";
				}

				@Override
				public String getBName() {
					return "cname version of bname";
				}

			};
		}

		@Override
		public void cleanUp() {
			System.out.println("CTImpl.producer: cleanUp was called");
		}
	};

	@Override
	protected IProducer<C> getProducer() {
		return producer;
	}

	@After
	public final void cleanupCTImpl() {
		producer.cleanUp();
	}

	@Test
	public void additionalTest() {
		System.out
				.println("Additional test ran (no producer.newInstance() not called)");
	}

}
