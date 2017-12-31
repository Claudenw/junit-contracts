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

import org.junit.After;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

/**
 * An example Contract test for D interface.
 * 
 * Defining DT as a generic class with the type extending the type we are
 * testing (e.g. DT&lt;T extends D&gt;) ensures that there are no issues with
 * using derived classes in the tests.
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 * @param <T>
 *            The class we are testing. Must implement D.
 */
// Define this as the contract test for the D interface
@Contract(D.class)
public class DT<T extends D> {

    // the producer for the tests
    private IProducer<T> producer;

    /**
     * Set the producer we will use for this test.
     * 
     * @param producer
     *            The producer we will use for this test.
     */
    @Contract.Inject
    public final void setProducer(IProducer<T> producer) {
        this.producer = producer;
    }

    protected final IProducer<T> getProducer() {
        return producer;
    }

    /**
     * Cleanup the producer.
     */
    @After
    public final void cleanupCT() {
        producer.cleanUp();
    }

    /**
     * Test getDName()
     */
    @ContractTest
    public void testGetDName() {
        Listener.add( getProducer().newInstance().getDName() );
    }

    /**
     * Test getA()
     */
    @ContractTest
    public void testGetA() {
        Listener.add( getProducer().newInstance().getA().toString() );
    }

    /**
     * Test getB()
     */
    @ContractTest
    public void testGetB() {
        Listener.add( getProducer().newInstance().getB().toString() );
    }
}
