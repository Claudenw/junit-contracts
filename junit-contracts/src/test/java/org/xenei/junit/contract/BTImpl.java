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
 * concrete example of BT implementation
 * 
 */
public class BTImpl extends BT {

	private IProducer<B> producer = new IProducer<B>() {

		@Override
		public B newInstance() {
			System.out.println("BTImpl.producer: newInstance was called");
			return new B() {
				@Override
				public String getBName() {
					return "bname";
				}
			};
		}

		@Override
		public void cleanUp() {
			System.out.println("BTImpl.producer: cleanUp was called");
		}

	};

	@Override
	protected IProducer<B> getProducer() {
		return producer;
	}

	@After
	public final void cleanupBTImpl() {
		producer.cleanUp();
	}
}
