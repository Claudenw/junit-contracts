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

import org.junit.runner.RunWith;
import org.xenei.junit.bad.BadNoInject;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.Dynamic;
import org.xenei.junit.contract.IProducer;

/**
 * An example Dynamic Contract Suite test for the D interface.
 * 
 * Note that this class implements Dynamic. This means that it will run like a
 * standard JUnit Suite except that the names of the classes to execute are not
 * provided until after the Suite is created. This allows us to create a
 * producer and pass it to the tests in the suite.
 * 
 * Any test in the suite that is annotated with RunWith(ContractSuite.class)
 * will be handled as follows:
 * <ol>
 * <li>The Dynamic.Inject annotated method of the Suite will be called</li>
 * <li>The Contract.Inject annotated setter method of the suite test will be
 * called with the result of the above step as the argument</li>
 * <li>The Contract.Inject annotated getter method of the suite test will be
 * called to get the producer for the associated contract tests.</li>
 * </ol>
 */
// run as a contract test
@RunWith(ContractSuite.class)
// testing the CImpl class.
@ContractImpl(value = DImpl.class, ignore = { BadNoInject.class })
public class DTImplSuite implements Dynamic {

	// the producer for DImpl
	private IProducer<DImpl> producer = new IProducer<DImpl>() {

		@Override
		public DImpl newInstance() {
			return new DImpl();
		}

		@Override
		public void cleanUp() {
		}

	};

	/**
	 * The method that indicates the master producer for the dynamic tests. This
	 * producer will be injected into all of the other ContractSuite run tests
	 * in this suite.
	 * 
	 * @return The producer that will be injected.
	 */
	@Dynamic.Inject
	public IProducer<DImpl> getInjected() {
		return producer;
	}

	/**
	 * The lists of classes in the suite.
	 */
	@Override
	public List<Class<?>> getSuiteClasses() {
		return Arrays.asList(new Class<?>[] { DTest.class, ATest.class,
				BTest.class });
	}

	/**
	 * The DImpl contract test implementation.
	 * 
	 * This is protected to keep some test runners from executing it outside the
	 * contract suite test.
	 * 
	 */
	@ContractImpl(DImpl.class)
	@RunWith(ContractSuite.class)
	protected static class DTest {
		// Uses the producer directly,
		private IProducer<DImpl> producerD;

		// public constructor required by JUnit
		public DTest() {
		};

		/**
		 * The setter that accepts the producer from the enclosing test.
		 * 
		 * @param producerD
		 *            the producer of DImpl objects.
		 */
		@Contract.Inject
		public void setProducer(IProducer<DImpl> producerD) {
			this.producerD = producerD;
		}

		/**
		 * The getter that converts the producer from the enclosing test to the
		 * producer needed for this test.
		 * 
		 * In this case just returns the producer passed in the setter.
		 * 
		 * @return The producer passed in the setter.
		 */
		@Contract.Inject
		public IProducer<DImpl> getProducer() {
			return producerD;
		}
	}

	/**
	 * See the ATest documentation.
	 */
	interface ForceA extends A {
	};

	/**
	 * The A contract test implementation.
	 * 
	 * There is a trick here.
	 * <ol>
	 * <li>DImpl returns an implementation of A so we don't know the actual
	 * implementing class.</li>
	 * <li>ContractImpl specifies a class under test so the ContractSuite
	 * framework only tests the interfaces of class specified in the
	 * ContractImpl</li>
	 * <li>Therefore, passing A.class to the ContractImpl will not find any
	 * tests as there are no super interfaces of A.</li>
	 * </ol>
	 * <p>
	 * The solution is to create an interface that extends the interface we want
	 * to test ("ForceA" in this case) so that the ContractImpl will find it.
	 * </p>
	 * <p>
	 * Effectively this class converts the IProducer&lt;DImpl&gt; into a
	 * IProducer&lt;A&gt; for the contract tests.
	 * </p>
	 * <p>
	 * This is protected to keep some test runners from executing it outside the
	 * contract suite test.
	 * </p>
	 * 
	 */
	@ContractImpl(ForceA.class)
	@RunWith(ContractSuite.class)
	protected static class ATest {
		// the master producer
		private IProducer<DImpl> producerD;

		// the producer we need for the A interface test
		private IProducer<A> producer;

		/**
		 * JUnit requires a public constructor
		 */
		public ATest() {
		};

		/**
		 * The method that will be called by the enclosing test to set the
		 * producer
		 * 
		 * @param producerD
		 *            the master producer from the enclosing test.
		 */
		@Contract.Inject
		public void setProducer(IProducer<DImpl> producerD) {
			this.producerD = producerD;

			producer = new IProducer<A>() {

				@Override
				public A newInstance() {
					return ATest.this.producerD.newInstance().getA();
				}

				@Override
				public void cleanUp() {
					// does nothing

				}
			};
		}

		/**
		 * The method called by the test implementations to get the producer.
		 * 
		 * @return the producer of A objects.
		 */
		@Contract.Inject
		public IProducer<A> getProducer() {
			return producer;
		}
	}

	/**
	 * See the BTest documentation
	 * 
	 */
	interface ForceB extends B {
	};

	/**
	 * The B contract test implementation.
	 * 
	 * There is a trick here.
	 * <ol>
	 * <li>DImpl returns an implementation of B so we don't know the actual
	 * implementing class.</li>
	 * <li>ContractImpl specifies a class under test so the ContractSuite
	 * framework only tests the interfaces of class specified in the
	 * ContractImpl</li>
	 * <li>Therefore, passing B.class to the ContractImpl will not find any
	 * tests as there are no super interfaces of B.</li>
	 * </ol>
	 * <p>
	 * The solution is to create an interface that extends the interface we want
	 * to test ("ForceB" in this case) so that the ContractImpl will find it.
	 * </p>
	 * <p>
	 * Effectively this class converts the IProducer&lt;DImpl&gt; into a
	 * IProducer&lt;B&gt; for the contract tests.
	 * </p>
	 * <p>
	 * This is protected to keep some test runners from executing it outside the
	 * contract suite test.
	 * </p>
	 * 
	 */
	@ContractImpl(ForceB.class)
	@RunWith(ContractSuite.class)
	protected static class BTest {
		private IProducer<DImpl> producerD;

		private IProducer<B> producer;

		public BTest() {
		};

		/**
		 * The method that will be called by the enclosing test to set the
		 * producer
		 * 
		 * @param producerD
		 *            the master producer from the enclosing test.
		 */
		@Contract.Inject
		public void setProducer(IProducer<DImpl> producerD) {
			this.producerD = producerD;

			producer = new IProducer<B>() {

				@Override
				public B newInstance() {
					return BTest.this.producerD.newInstance().getB();
				}

				@Override
				public void cleanUp() {
					// does nothing.

				}
			};
		}

		/**
		 * The method called by the test implementations to get the producer.
		 * 
		 * @return The producer of B objects
		 */
		@Contract.Inject
		public IProducer<B> getProducer() {
			return producer;
		}
	}
}
