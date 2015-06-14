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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base String filter, that converts class to class name for accept evaluation.
 *
 */
public abstract class AbstractStringClassFilter extends AbstractBaseClassFilter {

	private final List<String> strings = new ArrayList<String>();

	/** Whether the comparison is case sensitive. */
	protected final Case caseSensitivity;

	/**
	 * Constructs a new case-sensitive name class filter for a single name.
	 * 
	 * @param str
	 *            the string to allow, must not be null
	 * @throws IllegalArgumentException
	 *             if the str is null
	 */
	protected AbstractStringClassFilter(String str) {
		this(null, str);
	}

	/**
	 * Construct a new name class filter specifying case-sensitivity.
	 *
	 * @param str
	 *            the string to allow, must not be null
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null = case-sensitive
	 * @throws IllegalArgumentException
	 *             if the name is null
	 */
	protected AbstractStringClassFilter(Case caseSensitivity, String str) {
		this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE
				: caseSensitivity;
		addString(str);
	}

	/**
	 * Constructs a new case-sensitive name class filter for an array of names.
	 * <p>
	 * The array is not cloned, so could be changed after constructing the
	 * instance. This would be inadvisable however.
	 * 
	 * @param strings
	 *            the array of strings to allow, must not be null or zero length
	 * @throws IllegalArgumentException
	 *             if the names array is null
	 */
	protected AbstractStringClassFilter(String... strings) {
		this(null, strings);
	}

	/**
	 * Constructs a new name class filter for an array of names specifying
	 * case-sensitivity.
	 * <p>
	 * The array is not cloned, so could be changed after constructing the
	 * instance. This would be inadvisable however.
	 * 
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 * @param strings
	 *            the array of strings to allow, must not be null or zero length
	 * @throws IllegalArgumentException
	 *             if the names array is null
	 */
	protected AbstractStringClassFilter(Case caseSensitivity, String... strings) {
		if (strings == null || strings.length < 1) {
			throw new IllegalArgumentException(
					"List of strings may not be null or null length");
		}
		this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE
				: caseSensitivity;
		addStrings(strings);
	}

	/**
	 * Constructs a new case-sensitive name class filter for a collection of
	 * names.
	 * 
	 * @param strings
	 *            the array of strings to allow, must not be null or zero length
	 * @throws IllegalArgumentException
	 *             if the name list is null
	 * @throws ClassCastException
	 *             if the list does not contain Strings
	 */
	protected AbstractStringClassFilter(Collection<String> strings) {
		this(null, strings.toArray(new String[strings.size()]));
	}

	/**
	 * Constructs a new name class filter for a collection of names specifying
	 * case-sensitivity.
	 * 
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 * @param strings
	 *            the strings to check against.
	 * @throws IllegalArgumentException
	 *             if the name list is null
	 * @throws ClassCastException
	 *             if the list does not contain Strings
	 */
	protected AbstractStringClassFilter(Case caseSensitivity,
			Collection<String> strings) {
		this(caseSensitivity, strings.toArray(new String[strings.size()]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] args() {
		String[] retval = new String[strings.size() + 1];
		retval[0] = caseSensitivity.getName();
		for (int i = 0; i < strings.size(); i++) {
			retval[i + 1] = strings.get(i);
		}
		return retval;
	}

	/**
	 * Add a string to the filter.
	 * 
	 * @param str
	 *            the string to add.
	 */
	public final void addString(String str) {
		if (str == null) {
			throw new IllegalArgumentException("The string must not be null");
		}
		strings.add(str);
	}

	/**
	 * Add a collection of strings to the filter. Strings will be added in the
	 * order the collection iterator returns them.
	 * 
	 * @param strings
	 *            the collection of strings to be added.
	 */
	public final void addStrings(Collection<String> strings) {
		if (strings == null) {
			throw new IllegalArgumentException("The strings must not be null");
		}
		for (String s : strings) {
			addString(s);
		}
	}

	/**
	 * Add an array of strings to the filter.
	 * 
	 * @param strings
	 *            The strings to add.
	 */
	public final void addStrings(String... strings) {
		if (strings == null) {
			throw new IllegalArgumentException("The strings must not be null");
		}
		for (String s : strings) {
			addString(s);
		}
	}

	/**
	 * Get the list of strings from the filter.
	 * 
	 * @return An unmodifiableList of strings.
	 */
	public final List<String> getStrings() {
		return Collections.unmodifiableList(this.strings);
	}

	/**
	 * Converts class to name and calls accept( className )
	 * 
	 * @return true if the class name passes the filter.
	 */
	@Override
	public boolean accept(Class<?> clazz) {
		return accept(clazz.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return ClassFilter.Util.toString(this);
	}
}
