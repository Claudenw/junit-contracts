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
import java.util.List;

/**
 * A filter that matches classes by prefix.
 */
public class PrefixClassFilter extends AbstractStringClassFilter implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5764899732969345726L;

	/** The filename prefixes to search for */
    private final String[] prefixes;

    /** Whether the comparison is case sensitive. */
    private final Case caseSensitivity;

    /**
     * Constructs a new Prefix class filter for a single prefix.
     * 
     * @param prefix  the prefix to allow, must not be null
     * @throws IllegalArgumentException if the prefix is null
     */
    public PrefixClassFilter(String prefix) {
        this(prefix, Case.SENSITIVE);
    }

    /**
     * Constructs a new Prefix class filter for a single prefix 
     * specifying case-sensitivity.
     * 
     * @param prefix  the prefix to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     */
    public PrefixClassFilter(String prefix, Case caseSensitivity) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        this.prefixes = new String[] {prefix};
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new Prefix class filter for any of an array of prefixes.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     * 
     * @param prefixes  the prefixes to allow, must not be null
     * @throws IllegalArgumentException if the prefix array is null
     */
    public PrefixClassFilter(String[] prefixes) {
        this(prefixes, Case.SENSITIVE);
    }

    /**
     * Constructs a new Prefix class filter for any of an array of prefixes
     * specifying case-sensitivity.
     * <p>
     * The array is cloned.
     * 
     * @param prefixes  the prefixes to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     */
    public PrefixClassFilter(String[] prefixes, Case caseSensitivity) {
        if (prefixes == null) {
            throw new IllegalArgumentException("The array of prefixes must not be null");
        }
        this.prefixes = new String[prefixes.length];
        System.arraycopy(prefixes, 0, this.prefixes, 0, prefixes.length);
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new Prefix class filter for a list of prefixes.
     * 
     * @param prefixes  the prefixes to allow, must not be null
     * @throws IllegalArgumentException if the prefix list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public PrefixClassFilter(List<String> prefixes) {
        this(prefixes, Case.SENSITIVE);
    }

    /**
     * Constructs a new Prefix class filter for a list of prefixes
     * specifying case-sensitivity.
     * 
     * @param prefixes  the prefixes to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the prefix list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public PrefixClassFilter(List<String> prefixes, Case caseSensitivity) {
        if (prefixes == null) {
            throw new IllegalArgumentException("The list of prefixes must not be null");
        }
        this.prefixes = prefixes.toArray(new String[prefixes.size()]);
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Checks to see if the filename starts with the prefix.
     * 
     * @param file  the File to check
     * @return true if the filename starts with one of our prefixes
     */
    @Override
    public boolean accept(String className) {
        for (String prefix : this.prefixes) {
            if (caseSensitivity.checkStartsWith(className, prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        if (prefixes != null) {
            for (int i = 0; i < prefixes.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(prefixes[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
    
}
