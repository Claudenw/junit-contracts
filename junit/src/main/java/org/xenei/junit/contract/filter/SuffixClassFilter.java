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
import java.util.Collection;

/**
 * A filter to match the suffix of a class name.
 */
public class SuffixClassFilter extends AbstractStringClassFilter implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 525854048564445111L;

	/**
	 * Constructs a new Suffix class filter for a single extension.
	 * 
	 * @param suffix
	 *            the suffix to allow, must not be null
	 * @throws IllegalArgumentException
	 *             if the suffix is null
	 */
	public SuffixClassFilter(String suffix) {
		super(suffix);
	}

	/**
	 * Constructs a new Suffix class filter for a single extension specifying
	 * case-sensitivity.
	 *
	 * @param suffix
	 *            the suffix to allow, must not be null
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 */
	public SuffixClassFilter(Case caseSensitivity, String suffix) {
		super(caseSensitivity, suffix);
	}

	/**
	 * Constructs a new Suffix class filter for an array of suffixs.
	 * <p>
	 * The array is not cloned, so could be changed after constructing the
	 * instance. This would be inadvisable however.
	 * 
	 * @param suffixes
	 *            the suffixes to allow, must not be null
	 * @throws IllegalArgumentException
	 *             if the suffix array is null
	 */
	public SuffixClassFilter(String... suffixes) {
		super(suffixes);
	}

	/**
	 * Constructs a new Suffix class filter for an array of suffixs specifying
	 * case-sensitivity.
	 * <p>
	 * 
	 * 
	 * @param suffixes
	 *            the suffixes to allow, must not be null
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 * @throws IllegalArgumentException
	 *             if the suffix array is null
	 */
	public SuffixClassFilter(Case caseSensitivity, String... suffixes) {
		super(caseSensitivity, suffixes);
	}

	/**
	 * Constructs a new Suffix file filter for a collection of suffixes.
	 * 
	 * @param suffixes
	 *            the suffixes to allow, must not be null
	 * @throws IllegalArgumentException
	 *             if the suffix list is null
	 * @throws ClassCastException
	 *             if the list does not contain Strings
	 */
	public SuffixClassFilter(Collection<String> suffixes) {
		super(suffixes);
	}

	/**
	 * Constructs a new Suffix class filter for a collection of suffixes
	 * specifying case-sensitivity.
	 * 
	 * @param suffixes
	 *            the suffixes to allow, must not be null
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 * @throws IllegalArgumentException
	 *             if the suffix list is null
	 */
	public SuffixClassFilter(Case caseSensitivity, Collection<String> suffixes) {
		super(caseSensitivity, suffixes);
	}

	/**
	 * Checks to see if the class name ends with the suffix.
	 * 
	 * @param className
	 *            the class name to check
	 * 
	 * @return true if the filename ends with one of our suffixes
	 */
	@Override
	public boolean accept(String className) {
		for (String suffix : getStrings()) {
			if (caseSensitivity.checkEndsWith(className, suffix)) {
				return true;
			}
		}
		return false;
	}
}
