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

/**
 * Aconcrete implementation of C with an extra method to test
 * 
 */
public class CImpl2 implements C {
    @Override
    public String getCName() {
        return "cname";
    }

    @Override
    public String getAName() {
        return "cname version of aname";
    }

    @Override
    public String getBName() {
        return "cname version of bname";
    }

    @Override
    public String toString() {
        return "CImpl2";
    }

    @Override
    public int getBInt() {
        return 3;
    }

    /**
     * An extra method in this implementation that is not defined in C.
     * 
     * @return the string "called Extra Method";
     */
    public String extraMethod() {
        return "called Extra Method";
    }
}
