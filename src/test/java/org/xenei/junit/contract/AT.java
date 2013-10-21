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
 * example Contract test for Foo interface.
 * 
 */
@Ignore
@Contract(A.class)
public class AT<T extends A> {

	private IProducer<T> producer;
	
	@Contract.Inject
	public final void setProducer(IProducer<T> producer)
	{
		this.producer = producer;
	}
	
	protected final IProducer<T> getProducer() {
		return producer;
	}

	@After
	public final void cleanupAT() {
		getProducer().cleanUp();
	}

	@Test
	public void testGetAName() {
		Listener.add(getProducer().newInstance().getAName());
	}
	
	
}
