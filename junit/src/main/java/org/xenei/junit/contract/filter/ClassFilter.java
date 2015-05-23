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

/**
 * Interface that defines a ClassFilter.=
 *
 */
public interface ClassFilter {
	/**
	 * Accept a class.
	 * @param clazz the class to accept.
	 * @return True if the class matches the filter, false otherwise.
	 */
	boolean accept(Class<?> clazz);
	/**
	 * Accept a class name.  In some cases this is a string compare in other cases the class is
	 * loaded from the class loader and other comparisons made.
	 * @param className the class name to accept.
	 * @return True if the class matches the filter, false otherwise.
	 */
	boolean accept(String className);
}
