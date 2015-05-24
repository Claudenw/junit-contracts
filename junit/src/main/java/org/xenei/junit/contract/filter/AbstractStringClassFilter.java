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
import java.util.Collections;
import java.util.List;

/**
 * Base String filter, that converts class to class name for accept evaluation.
 *
 */
public abstract class AbstractStringClassFilter extends AbstractBaseClassFilter {
	
	private final List<String> strings;
	
    /** Whether the comparison is case sensitive. */
    protected final Case caseSensitivity;
    
    /**
     * Constructs a new case-sensitive name class filter for a single name.
     * 
     * @param str  the string to allow, must not be null
     * @throws IllegalArgumentException if the str is null
     */
    protected AbstractStringClassFilter() {
    	this((Case)null);
    }
    
    protected AbstractStringClassFilter(Case caseSensitivity) {
    	this.strings = new ArrayList<String>();
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
    }
    
    /**
     * Constructs a new case-sensitive name class filter for a single name.
     * 
     * @param str  the string to allow, must not be null
     * @throws IllegalArgumentException if the str is null
     */
    protected AbstractStringClassFilter(String str) {
        this();
        addString( str );
    }

    /**
     * Construct a new name class filter specifying case-sensitivity.
     *
     * @param str  the string to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name is null
     */
    protected AbstractStringClassFilter(Case caseSensitivity, String str) {
    	this(caseSensitivity);
    	addString( str );
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
    protected AbstractStringClassFilter(String... strings) {
        this();
        addStrings( strings);
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
    protected AbstractStringClassFilter(Case caseSensitivity, String... strings) {
    	this( caseSensitivity );
    	addStrings( strings );
    }

    /**
     * Constructs a new case-sensitive name class filter for a list of names.
     * 
     * @param names  the names to allow, must not be null
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    protected AbstractStringClassFilter(List<String> strings) {
        this();
        addStrings( strings );
    }

    /**
     * Constructs a new name class filter for a list of names specifying case-sensitivity.
     * 
     * @param names  the names to allow, must not be null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the name list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    protected AbstractStringClassFilter(Case caseSensitivity, List<String> strings) {
    	this(caseSensitivity);
    	addStrings( strings );
    }

    public final void addString(String str)
    {
    	 if (str == null) {
             throw new IllegalArgumentException("The string must not be null");
         }
    	 strings.add(str);
    }
    
    public final void addStrings(List<String> strings)
    {
    	 if (strings == null) {
             throw new IllegalArgumentException("The strings must not be null");
         }
    	 for (String s : strings)
    	 {
    		 addString( s );
    	 }
    }
	
    public final void addStrings(String... strings)
    {
    	 if (strings == null) {
             throw new IllegalArgumentException("The strings must not be null");
         }
    	 for (String s : strings)
    	 {
    		 addString( s );
    	 }
    }
    
    protected final List<String> getStrings() {
		return Collections.unmodifiableList(this.strings);
	}
    
	@Override
	public boolean accept(Class<?> clazz)
	{
		return accept( clazz.getName());
	}   

	 /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        String[] parts = getClass().getName().split( "\\." );

        String name = parts[parts.length-1];
        buffer.append(String.format( "%s[%s](",name, caseSensitivity.toString().charAt(0)));
        
        for (int i = 0; i < strings.size(); i++) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append(strings.get(i));
        }
        
        buffer.append(")");
        return buffer.toString();
    }
}
