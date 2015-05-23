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
 * A filter to match the suffix of a class name.
 */
public class SuffixClassFilter extends AbstractStringClassFilter implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 525854048564445111L;

	/** The filename suffixes to search for */
    private final String[] suffixes;

    /** Whether the comparison is case sensitive. */
    private final Case caseSensitivity;

    /**
     * Constructs a new Suffix class filter for a single extension.
     * 
     * @param suffix  the suffix to allow, must not be null
     * @throws IllegalArgumentException if the suffix is null
     */
    public SuffixClassFilter(String suffix) {
        this(suffix, Case.SENSITIVE);
    }

    /**
     * Constructs a new Suffix class filter for a single extension
     * specifying case-sensitivity.
     *
     * @param suffix  the suffix to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive

     */
    public SuffixClassFilter(String suffix, Case caseSensitivity) {
        if (suffix == null) {
            throw new IllegalArgumentException("The suffix must not be null");
        }
        this.suffixes = new String[] {suffix};
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new Suffix class filter for an array of suffixs.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     * 
     * @param suffixes  the suffixes to allow, must not be null
     * @throws IllegalArgumentException if the suffix array is null
     */
    public SuffixClassFilter(String[] suffixes) {
        this(suffixes, Case.SENSITIVE);
    }

    /**
     * Constructs a new Suffix class filter for an array of suffixs
     * specifying case-sensitivity.
     * <p>
     * 
     * 
     * @param suffixes  the suffixes to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the suffix array is null
     */
    public SuffixClassFilter(String[] suffixes, Case caseSensitivity) {
        if (suffixes == null) {
            throw new IllegalArgumentException("The array of suffixes must not be null");
        }
        this.suffixes = new String[suffixes.length];
        System.arraycopy(suffixes, 0, this.suffixes, 0, suffixes.length);
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new Suffix file filter for a list of suffixes.
     * 
     * @param suffixes  the suffixes to allow, must not be null
     * @throws IllegalArgumentException if the suffix list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public SuffixClassFilter(List<String> suffixes) {
        this(suffixes, Case.SENSITIVE);
    }

    /**
     * Constructs a new Suffix class filter for a list of suffixes
     * specifying case-sensitivity.
     * 
     * @param suffixes  the suffixes to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the suffix list is null
     */
    public SuffixClassFilter(List<String> suffixes, Case caseSensitivity) {
        if (suffixes == null) {
            throw new IllegalArgumentException("The list of suffixes must not be null");
        }
        this.suffixes = suffixes.toArray(new String[suffixes.size()]);
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    
    /**
     * Checks to see if the class name ends with the suffix.
     * 
     * @param file  the File directory
     * @param name  the filename
     * @return true if the filename ends with one of our suffixes
     */
    @Override
    public boolean accept(String className) {
        for (String suffix : this.suffixes) {
            if (caseSensitivity.checkEndsWith(className, suffix)) {
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
        if (suffixes != null) {
            for (int i = 0; i < suffixes.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(suffixes[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
    
}
