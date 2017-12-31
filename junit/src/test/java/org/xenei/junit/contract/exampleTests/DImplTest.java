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
 * Show that DT executes correctly as a concrete implementation.
 * 
 * This will only run the tests defined in DT without running any other
 * interface tests. Compare this to DImplContractTest
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 */
@RunWith(ContractTestRunner.class)
public class DImplTest extends DT<DImpl> {

    /**
     * Constructor
     */
    public DImplTest() {
        setProducer( new IProducer<DImpl>() {

            @Override
            public DImpl newInstance() {
                Listener.add( "DImplTest.producer.newInstance()" );
                return new DImpl();
            }

            @Override
            public void cleanUp() {
                Listener.add( "DImplTest.producer.cleanUp()" );
            }

        } );
    }

    /**
     * Clear the listener for tests.
     */
    @BeforeClass
    public static void beforeClass() {
        Listener.clear();
    }

    /**
     * Verify the listener saw all the expected events.
     */
    @AfterClass
    public static void afterClass() {
        final String[] expected = { "DImplTest.producer.newInstance()", "dname", "DImplTest.producer.cleanUp()",
                "DImplTest.producer.newInstance()", "AImpl", "DImplTest.producer.cleanUp()",
                "DImplTest.producer.newInstance()", "BImpl", "DImplTest.producer.cleanUp()" };

        final List<String> l = Listener.get();
        Assert.assertEquals( l, Arrays.asList( expected ) );

    }
}
