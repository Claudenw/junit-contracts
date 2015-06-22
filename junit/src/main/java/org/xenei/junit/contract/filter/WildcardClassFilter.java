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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Filters classes using the supplied wildcards.
 * <p>
 * This filter selects classes one or more wildcards. Testing is case-sensitive
 * by default, but this can be configured.
 * <p>
 * The wildcard matcher uses the characters '?' and '*' to represent a single or
 * multiple characters. This is the same as often found on Dos/Unix command
 * lines.
 * <p>
 * For example:
 * 
 * <pre>
 * Class&lt;?&gt; clazz = org.xenei.junit.contract.filter.ClassFilter.class;
 * ClassFilter filter = new WildcardClassFilter(&quot;*.filter.*&quot;);
 * if (filter.accept(clazz)) {
 * 	System.out.println(&quot;it works&quot;);
 * }
 * </pre>
 */
public class WildcardClassFilter extends AbstractBaseClassFilter implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4689016340648211889L;
	/** The wildcards that will be used to match filenames. */
	private final List<String> wildcards = new ArrayList<String>();
	/** Whether the comparison is case sensitive. */
	private final Case caseSensitivity;

	private ClassFilter wrapped;

	/**
	 * Construct a new case-sensitive wildcard filter for a single wildcard.
	 *
	 * @param wildcard
	 *            the wildcard to match
	 * @throws IllegalArgumentException
	 *             if the pattern is null
	 */
	public WildcardClassFilter(String wildcard) {
		this(null, wildcard);
	}

	/**
	 * Construct a new wildcard filter for a single wildcard specifying
	 * case-sensitivity.
	 *
	 * @param wildcard
	 *            the wildcard to match, not null
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 * @throws IllegalArgumentException
	 *             if the pattern is null
	 */
	public WildcardClassFilter(Case caseSensitivity, String wildcard) {
		this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE
				: caseSensitivity;
		addWildcard(wildcard);
	}

	/**
	 * Construct a new case-sensitive wildcard filter for an array of wildcards.
	 * <p>
	 * The array is not cloned, so could be changed after constructing the
	 * instance. This would be inadvisable however.
	 *
	 * @param wildcards
	 *            the array of wildcards to match
	 * @throws IllegalArgumentException
	 *             if the pattern array is null
	 */
	public WildcardClassFilter(String... wildcards) {
		this(null, wildcards);
	}

	/**
	 * Construct a new wildcard filter for an array of wildcards specifying
	 * case-sensitivity.
	 * <p>
	 * The array is not cloned, so could be changed after constructing the
	 * instance. This would be inadvisable however.
	 *
	 * @param wildcards
	 *            the array of wildcards to match, not null
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 * @throws IllegalArgumentException
	 *             if the pattern array is null
	 */
	public WildcardClassFilter(Case caseSensitivity, String... wildcards) {
		if (wildcards.length == 0) {
			throw new IllegalArgumentException(
					"The wildcard array may not be empty");
		}
		this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE
				: caseSensitivity;
		addWildCards(wildcards);
	}

	/**
	 * Construct a new case-sensitive wildcard filter for a collection of
	 * wildcards.
	 *
	 * @param wildcards
	 *            the list of wildcards to match, not null
	 * @throws IllegalArgumentException
	 *             if the pattern list is null
	 * @throws ClassCastException
	 *             if the list does not contain Strings
	 */
	public WildcardClassFilter(Collection<String> wildcards) {
		this(null, wildcards);
	}

	/**
	 * Construct a new wildcard filter for a collection of wildcards specifying
	 * case-sensitivity.
	 *
	 * @param wildcards
	 *            the list of wildcards to match, not null
	 * @param caseSensitivity
	 *            how to handle case sensitivity, null means case-sensitive
	 * @throws IllegalArgumentException
	 *             if the pattern list is null
	 * @throws ClassCastException
	 *             if the list does not contain Strings
	 */
	public WildcardClassFilter(Case caseSensitivity,
			Collection<String> wildcards) {
		if (wildcards == null || wildcards.size() == 0) {
			throw new IllegalArgumentException(
					"The wildcard list may not be empty");
		}
		this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE
				: caseSensitivity;
		addWildCards(wildcards);
	}

	/**
	 * Add a collection of wild card strings to the filter
	 * 
	 * @param wildcards
	 *            The list of wildcard strings.
	 * @throws IllegalArgumentException
	 *             if the list is null or if any string within the list is null.
	 */
	public void addWildCards(Collection<String> wildcards) {
		if (wildcards == null) {
			throw new IllegalArgumentException(
					"The wildcard list must not be null");
		}
		for (String s : wildcards) {
			addWildcard(s);
		}
	}

	/**
	 * Add an array of wild card strings to the filter
	 * 
	 * @param wildcards
	 *            The array of wildcard strings.
	 * @throws IllegalArgumentException
	 *             if the array is null or if any string within the list is
	 *             null.
	 */
	public void addWildCards(String... wildcards) {
		if (wildcards == null) {
			throw new IllegalArgumentException(
					"The wildcard array must not be null");
		}
		for (String s : wildcards) {
			addWildcard(s);
		}
	}

	/**
	 * Add a single wildcard string to the filter.
	 * 
	 * @param wildcard
	 *            the string to add.
	 * @throws IllegalArgumentException
	 *             if the wildcard is null.
	 */
	public void addWildcard(String wildcard) {
		if (wildcard == null) {
			throw new IllegalArgumentException("The wildcard must not be null");
		}
		this.wildcards.add(wildcard);
		RegexClassFilter filter = new RegexClassFilter(caseSensitivity,
				makeRegex(wildcard));
		if (wrapped == null) {
			wrapped = filter;
		} else {
			if (wrapped instanceof RegexClassFilter) {
				wrapped = new OrClassFilter(wrapped, filter);
			} else {
				((OrClassFilter) wrapped).addClassFilter(filter);
			}
		}
	}

	/**
	 * Provide a String representation of this file filter.
	 *
	 * @return a String representation
	 */
	@Override
	public String toString() {
		return ClassFilter.Util.toString(this);
	}

	private static StringBuilder escapeString(StringBuilder sb, String s) {
		if (s != null && s.length() != 0) {
			sb.append(Pattern.quote(s));
		}
		return sb;
	}

	private static void parseWildAsterisk(StringBuilder sb, String s) {
		String[] blocks = s.split("\\*");
		Iterator<String> iter = Arrays.asList(blocks).iterator();
		if (iter.hasNext())
		{
			escapeString(sb, iter.next());
			while (iter.hasNext()) {
				sb = escapeString(sb.append(".*"), iter.next());
			}
			if (s.endsWith("*")) {
				sb.append(".*");
			}
		}
		else
		{
			if (s.equals("*"))
			{
				sb.append(".*");
			}
		}
	}

	private static StringBuilder parseWildQuestion(StringBuilder sb, String s) {
		String[] blocks = s.split("\\?");
		Iterator<String> iter = Arrays.asList(blocks).iterator();
		if (iter.hasNext())
		{
			parseWildAsterisk(sb, iter.next());
			while (iter.hasNext()) {
				sb.append(".");
				parseWildAsterisk(sb, iter.next());
			}
			if (s.endsWith("?")) {
				sb.append(".");
			}
		}
		else {
			if (s.equals("?"))
			{
				sb.append(".");
			}
		}
		return sb;
	}

	/**
	 * Convert the wildcard string into a regex string. Wildcards use '*' to
	 * denote multiple characters and '?' to denot a single character.
	 * 
	 * @param wildcard
	 *            The wild card string to convert.
	 * @return the corresponding regular expression string.
	 */
	public static String makeRegex(String wildcard) {
		if (wildcard == null) {
			throw new IllegalArgumentException("The wildcard must not be null");
		}
		return parseWildQuestion(new StringBuilder("^"), wildcard).append("$")
				.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(Class<?> clazz) {
		return wrapped.accept(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(String className) {
		return wrapped.accept(className);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] args() {
		String[] retval = new String[wildcards.size() + 1];
		retval[0] = caseSensitivity.toString();
		for (int i = 0; i < wildcards.size(); i++) {
			retval[i + 1] = wildcards.get(i);
		}
		return retval;
	}

}
