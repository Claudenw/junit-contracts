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
 * A class filter that always returns false.
 *
 */
public class FalseClassFilter extends AbstractBaseClassFilter implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5537028986673047572L;
	/**
	 * Singleton instance of false filter.
	 */
	public static final ClassFilter FALSE = new FalseClassFilter();

	/**
	 * Restrictive constructor.
	 */
	private FalseClassFilter() {
	}

	/**
	 * Returns false.
	 *
	 * @param fileName
	 *            the file name to check (ignored)
	 * @return false
	 */
	@Override
	public boolean accept(String fileName) {
		return false;
	}

	/**
	 * Returns false.
	 *
	 * @param clazz
	 *            the class to check (ignored)
	 * @return false
	 */
	@Override
	public boolean accept(Class<?> clazz) {
		return false;
	}

	@Override
	public String[] args() {
		return NO_ARGS;
	}
}
