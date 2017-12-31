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
 * Show that AT executes correctly as a concrete implementation.
 * 
 * This will only run the tests defined in AT without running any other
 * interface tests. Compare this to AImplContractTest
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 */
@RunWith(ContractTestRunner.class)
public class AImplTest extends AT<AImpl> {

    /**
     * Constructor
     */
    public AImplTest() {
        // set the producer
        setProducer( new IProducer<AImpl>() {

            @Override
            public AImpl newInstance() {
                Listener.add( "AImplTest.producer.newInstance()" );
                return new AImpl();
            }

            @Override
            public void cleanUp() {
                Listener.add( "AImplTest.producer.cleanUp()" );
            }

        } );
    }

    /**
     * Clear the listener so we can start again
     */
    @BeforeClass
    public static void beforeClass() {
        Listener.clear();
    }

    /**
     * Verify the listener detected all the expected events.
     */
    @AfterClass
    public static void afterClass() {
        final String[] expected = { "AImplTest.producer.newInstance()", "aname", "AImplTest.producer.cleanUp()" };

        final List<String> l = Listener.get();
        Assert.assertEquals( l, Arrays.asList( expected ) );

    }
}
