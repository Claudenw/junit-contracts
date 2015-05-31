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

package org.xenei.junit.contract.filter;

import java.util.Collection;

/**
 * Base class with simple toString implementation.
 *
 */
public abstract class AbstractBaseClassFilter implements ClassFilter {

	protected static final String[] NO_ARGS = new String[0];

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return ClassFilter.Util.toString(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String funcName() {
		String func = getClass().getSimpleName();
		if (func.endsWith("ClassFilter")) {
			return func.substring(0, func.length() - "ClassFilter".length());
		}
		if (func.endsWith("Filter")) {
			return func.substring(0, func.length() - "Filter".length());
		}
		return func;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Class<?>> filter(Collection<Class<?>> collection) {
		return ClassFilter.Util.filterClasses(collection, this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<String> filterNames(Collection<String> collection) {
		return ClassFilter.Util.filterClassNames(collection, this);
	}
}
