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
package org.xenei.junit.contract.filter;

import java.io.Serializable;

/**
 * A class filter that always returns true.
 *
 */
public class TrueClassFilter extends AbstractBaseClassFilter implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6422553815074269475L;
	/**
     * Singleton instance of true filter.
     */
    public static final ClassFilter TRUE = new TrueClassFilter();


    /**
     * Restrictive constructor.
     */
    private TrueClassFilter() {
    }

    /**
     * Returns true.
     *
     * @param clazz  the class to check (ignored)
     * @return true
     */
    @Override
	public boolean accept(Class<?> clazz) {
        return true;
    }

    /**
     * Returns true.
     *

     * @param className  the class name (ignored)
     * @return true
     */
    @Override
	public boolean accept(String className) {
        return true;
    }

	@Override
	public String[] args() {
		return NO_ARGS;
	}

}
