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
 * filters classes by name.
 * 
 */
public class NameClassFilter extends AbstractStringClassFilter implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2314511406134237664L;
	/** The filenames to search for */
    private final String[] names;
    /** Whether the comparison is case sensitive. */
    private final Case caseSensitivity;

    /**
     * Constructs a new case-sensitive name class filter for a single name.
     * 
     * @param name  the name to allow, must not be null
     * @throws IllegalArgumentException if the name is null
     */
    public NameClassFilter(String name) {
        this(name, null);
    }

    /**
     * Construct a new name class filter specifying case-sensitivity.
     *
     * @param name  the name to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name is null
     */
    public NameClassFilter(String name, Case caseSensitivity) {
        if (name == null) {
            throw new IllegalArgumentException("The name must not be null");
        }
        this.names = new String[] {name};
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new case-sensitive name class filter for an array of names.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     * 
     * @param names  the names to allow, must not be null
     * @throws IllegalArgumentException if the names array is null
     */
    public NameClassFilter(String[] names) {
        this(names, null);
    }

    /**
     * Constructs a new name class filter for an array of names specifying case-sensitivity.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     * 
     * @param names  the names to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the names array is null
     */
    public NameClassFilter(String[] names, Case caseSensitivity) {
        if (names == null) {
            throw new IllegalArgumentException("The array of names must not be null");
        }
        this.names = new String[names.length];
        System.arraycopy(names, 0, this.names, 0, names.length);
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Constructs a new case-sensitive name class filter for a list of names.
     * 
     * @param names  the names to allow, must not be null
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public NameClassFilter(List<String> names) {
        this(names, null);
    }

    /**
     * Constructs a new name class filter for a list of names specifying case-sensitivity.
     * 
     * @param names  the names to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public NameClassFilter(List<String> names, Case caseSensitivity) {
        if (names == null) {
            throw new IllegalArgumentException("The list of names must not be null");
        }
        this.names = names.toArray(new String[names.size()]);
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }

    /**
     * Checks to see if the name matches.
     * 
     * @param className  the class name to check
     * @return true if the filename matches
     */
    @Override
    public boolean accept(String className) {
        for (String name2 : this.names) {
            if (caseSensitivity.checkEquals(className, name2)) {
                return true;
            }
        }
        return false;
    } 

    /**
     * Provide a String representaion of this file filter.
     *
     * @return a String representaion
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getClass().getSimpleName());
        buffer.append("(");
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(names[i]);
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

}
