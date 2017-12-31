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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.xenei.junit.bad.BadNoInject;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractExclude;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

/**
 * Run the C tests using the contract suite runner.
 * 
 * This will run the tests defined in CT as well as AT (A contract tests) and BT
 * (B contract tests). Compare this to CImplTest.
 * 
 * Note that producer used for the AT and BT classes will be the
 * IProducer&lt;CImpl$gt; from this class.
 * 
 * The use of the Listener interface in the before and after methods are to
 * track that the tests are run correctly and in the proper order. This would
 * not be used in a production test but are part of our testing of
 * junit-contracts.
 * 
 */
// run as a contract test
@RunWith(ContractSuite.class)
// testing the CImpl class.
@ContractImpl(value = CImpl.class, ignore = { BadNoInject.class })
@ContractExclude(value = BT.class, methods = { "testGetBInt" })
public class CImplContractTestWithExclude {
    // the producer to use for all the tests
    private final IProducer<CImpl> producer = new IProducer<CImpl>() {
        @Override
        public CImpl newInstance() {
            Listener.add( "CImplContractTest.producer.newInstance()" );
            return new CImpl();
        }

        @Override
        public void cleanUp() {
            Listener.add( "CImplContractTest.producer.cleanUp()" );
        }
    };

    /**
     * The method to inject the producer into the test classes.
     * 
     * @return The producer we want to use for the tests.
     */
    @Contract.Inject
    public IProducer<CImpl> getProducer() {
        return producer;
    }

    /**
     * Clean up the listener for the tests.
     */
    @BeforeClass
    public static void beforeClass() {
        Listener.clear();
    }

    private static void verifyTest(List<String> expectedTests, List<String> results) {
        Assert.assertEquals( "CImplContractTest.producer.newInstance()", results.get( 0 ) );
        Assert.assertTrue( "Missing " + results.get( 1 ), expectedTests.contains( results.get( 1 ) ) );
        expectedTests.remove( results.get( 1 ) );
        Assert.assertEquals( "CImplContractTest.producer.cleanUp()", results.get( 2 ) );

    }

    /**
     * Verify that the Listener recorded all the expected events.
     */
    @AfterClass
    public static void afterClass() {
        final String[] testNames = { "cname", "cname version of bname", "cname version of aname" };
        final List<String> expectedTests = new ArrayList<String>( Arrays.asList( testNames ) );

        final List<String> l = Listener.get();

        for (int i = 0; i < testNames.length; i++) {
            final int j = i * 3;
            verifyTest( expectedTests, l.subList( j, j + 3 ) );
        }
        Assert.assertTrue( expectedTests.isEmpty() );

    }
}
