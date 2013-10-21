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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Example of ContractSuite for C interface using the CTImpl
 * 
 */
@RunWith(ContractSuite.class)
@ContractImpl(DImpl.class)
public class DTImplSuite implements Dynamic {

	private IProducer<DImpl> producer = new IProducer<DImpl>()
	{

		@Override
		public DImpl newInstance() {
			return new DImpl();
		}

		@Override
		public void cleanUp() {
		}
		
	};
	
	@Dynamic.Inject
	public IProducer<DImpl> getInjected() {
		return producer;
	}


	@Override
	public List<Class<?>> getSuiteClasses() {
		return Arrays.asList( new Class<?>[] { 
				DTest.class,
				ATest.class,
				BTest.class
		} );
	}
	
	@ContractImpl(DImpl.class)
	@RunWith(ContractSuite.class)
	protected static class DTest
	{
		private IProducer<DImpl> producerD;
	
		
		public DTest(){};
		
		@Contract.Inject
		public void setProducer( IProducer<DImpl> producerD )
		{
			this.producerD = producerD;
			
			
		}
		
		@Contract.Inject
		public IProducer<DImpl> getProducer()
		{
			return producerD;
		}
	}

	interface ForceA extends A{};
	@ContractImpl(ForceA.class)
	@RunWith(ContractSuite.class)
	protected static class ATest
	{
		private IProducer<DImpl> producerD;
	
		private IProducer<A> producer;
		
		public ATest(){};
		
		@Contract.Inject
		public void setProducer( IProducer<DImpl> producerD )
		{
			this.producerD = producerD;
			
			producer = new IProducer<A>(){
				
				@Override
				public A newInstance() {
					return ATest.this.producerD.newInstance().getA();
				}

				@Override
				public void cleanUp() {
					// TODO Auto-generated method stub
					
				}};
		}
		
		@Contract.Inject
		public IProducer<A> getProducer()
		{
			return producer;
		}
	}
	
	interface ForceB extends B{};
	@ContractImpl(ForceB.class)
	@RunWith(ContractSuite.class)
	protected static class BTest
	{
		private IProducer<DImpl> producerD;
	
		private IProducer<B> producer;
		
		public BTest(){};
		
		@Contract.Inject
		public void setProducer( IProducer<DImpl> producerD )
		{
			this.producerD = producerD;
			
			producer = new IProducer<B>(){
				
				@Override
				public B newInstance() {
					return BTest.this.producerD.newInstance().getB();
				}

				@Override
				public void cleanUp() {
					// TODO Auto-generated method stub
					
				}};
		}
		
		@Contract.Inject
		public IProducer<B> getProducer()
		{
			return producer;
		}
	}
}
