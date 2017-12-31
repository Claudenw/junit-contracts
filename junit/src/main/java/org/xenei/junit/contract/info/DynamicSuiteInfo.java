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

package org.xenei.junit.contract.info;

import java.lang.reflect.Method;

import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.Dynamic;
import org.xenei.junit.contract.MethodUtils;

/**
 * Handles dynamic suites.
 *
 * When executing a dynamic suite the dynamic.inject method should be called to
 * retrieve the instance to inject then the Contract.inject should be called to
 * inject it into the test.
 *
 */
public class DynamicSuiteInfo extends SuiteInfo {
    private final Method dynamicInjector;

    /**
     * Constructor
     *
     * @param dynamic
     *            The class under test.
     * @param impl
     *            The ContractImpl annotation for the class
     */
    public DynamicSuiteInfo(final Class<? extends Dynamic> dynamic, final ContractImpl impl) {
        super( dynamic, impl, MethodUtils.findAnnotatedGetter( impl.value(), Contract.Inject.class ) );
        dynamicInjector = MethodUtils.findAnnotatedGetter( dynamic, Dynamic.Inject.class );
        if (getMethod() == null) {
            addError( new IllegalArgumentException( "Classes that extends Dynamic [" + dynamic
                    + "] must contain a getter method annotated with @Dynamic.Inject" ) );

        }
    }

    /**
     * Get the method that returns the Dynamic IProducer.
     * 
     * @return the method that returns the Dynamic IProducer.
     */
    public Method getDynamicInjector() {
        return dynamicInjector;
    }
}