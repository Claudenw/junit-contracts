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
 * Example of Contract test for B interface.
 */
@Contract(B.class)
public class BT {

	private IProducer<B> producer;
	
	public BT()
	{
		this.producer = new IProducer<B>() {

			@Override
			public B newInstance() {
				Listener.add("BT.producer.newInstance()");
				return new BImpl();
			}

			@Override
			public void cleanUp() {
				Listener.add("BT.producer.cleanUp()");
			}

		};
	}
	
	@Contract.Inject
	public final void setProducer(IProducer<B> producer)
	{
		this.producer = producer;
	}
	
	protected final IProducer<B> getProducer() {
		return producer;
	}

	@Test
	public void testGetBName() {
		Listener.add(getProducer().newInstance().getBName());
	}
	
	@After
	public final void cleanupBT() {
		getProducer().cleanUp();
	}

}
