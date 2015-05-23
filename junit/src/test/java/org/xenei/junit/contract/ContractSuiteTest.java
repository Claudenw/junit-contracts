/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xenei.junit.contract;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.RunnerBuilder;
import org.mockito.ArgumentCaptor;
import org.xenei.junit.bad.BadAbstract;
import org.xenei.junit.bad.BadNoInject;

public class ContractSuiteTest {

	@Test
	public void testBadAbstract() throws Throwable {
		final RunnerBuilder builder = mock(RunnerBuilder.class);
		final ContractSuite cs = new ContractSuite(BadAbstractTest.class,
				builder);
		final RunNotifier notifier = mock(RunNotifier.class);
		cs.run(notifier);

		final ArgumentCaptor<Description> description = ArgumentCaptor
				.forClass(Description.class);
		verify(notifier).fireTestStarted(description.capture());
		assertEquals(BadAbstractTest.class.getName(), description.getValue()
				.getClassName());

		final ArgumentCaptor<Failure> failure = ArgumentCaptor
				.forClass(Failure.class);
		verify(notifier).fireTestFailure(failure.capture());
		final Throwable throwable = failure.getValue().getException();
		assertTrue("Should be illegal state exception",
				throwable instanceof IllegalStateException);
		assertEquals(
				"Classes annotated with @Contract (class org.xenei.junit.bad.BadAbstract) must not be abstract",
				throwable.getMessage());

		verify(notifier).fireTestFinished(description.capture());
		assertEquals(BadAbstractTest.class.getName(), description.getValue()
				.getClassName());

	}

	@Test
	public void testBadNoInject() throws Throwable {
		final RunnerBuilder builder = mock(RunnerBuilder.class);
		final ContractSuite cs = new ContractSuite(BadNoInjectTest.class,
				builder);
		final RunNotifier notifier = mock(RunNotifier.class);
		cs.run(notifier);

		final ArgumentCaptor<Description> description = ArgumentCaptor
				.forClass(Description.class);
		verify(notifier).fireTestStarted(description.capture());

		if (BadNoInject.class.getName().equals(
				description.getValue().getClassName())) {

			final ArgumentCaptor<Failure> failure = ArgumentCaptor
					.forClass(Failure.class);
			verify(notifier).fireTestFailure(failure.capture());
			final Throwable throwable = failure.getValue().getException();
			assertTrue("Should be illegal state exception",
					throwable instanceof IllegalStateException);
			assertEquals(
					"Classes annotated with @Contract (class org.xenei.junit.bad.BadNoInject) must include a @Contract.Inject annotation on a public non-abstract declared setter method",
					throwable.getMessage());
			verify(notifier).fireTestFinished(description.capture());
			assertEquals(BadNoInject.class.getName(), description.getValue()
					.getClassName());
		}
		else if (BadNoInjectTest.class.getName().equals(
				description.getValue().getClassName())) {
			verify(notifier).fireTestFinished(description.capture());
			assertEquals(BadNoInjectTest.class.getName(), description.getValue()
					.getClassName());	
		} else {
			fail("Unexpected description class name: "
					+ description.getValue().getClassName());
		}
	}

	@ContractImpl(value = BadNoInject.class)
	public static class BadNoInjectTest {
		// the producer to use for all the tests
		private final IProducer<BadNoInject> producer = new IProducer<BadNoInject>() {
			@Override
			public BadNoInject newInstance() {
				return new BadNoInject();
			}

			@Override
			public void cleanUp() {

			}
		};

		/**
		 * The method to inject the producer into the test classes.
		 */
		@Contract.Inject
		public IProducer<BadNoInject> getProducer() {
			return producer;
		}

		@ContractTest
		public void forceTest() {
			// just a test to meet the requirements.
		}

	}

	@ContractImpl(value = BadAbstract.class)
	public static class BadAbstractTest {
		// the producer to use for all the tests
		private final IProducer<BadAbstract> producer = new IProducer<BadAbstract>() {
			@Override
			public BadAbstract newInstance() {
				return new BadAbstract() {
				};
			}

			@Override
			public void cleanUp() {

			}
		};

		/**
		 * The method to inject the producer into the test classes.
		 */
		@Contract.Inject
		public IProducer<BadAbstract> getProducer() {
			return producer;
		}

	}
}
