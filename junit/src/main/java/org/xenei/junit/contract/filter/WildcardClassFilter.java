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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;


/**
 * Filters classes using the supplied wildcards.
 * <p>
 * This filter selects classes one or more wildcards.
 * Testing is case-sensitive by default, but this can be configured.
 * <p>
 * The wildcard matcher uses the characters '?' and '*' to represent a
 * single or multiple characters.
 * This is the same as often found on Dos/Unix command lines.
 * <p>
 * For example:
 * <pre>
 * Class<?> clazz = org.xenei.junit.contract.filter.ClassFilter.class;
 * ClassFilter filter = new WildcardClassFilter("*.filter.*");
 * if (filter.accept( clazz ))
 * {
 *   System.out.println( "it works");
 * }
 * </pre>
 */
public class WildcardClassFilter extends OrClassFilter implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4689016340648211889L;
	/** The wildcards that will be used to match filenames. */
    private final List<String> wildcards;
    /** Whether the comparison is case sensitive. */
    private final Case caseSensitivity;

    /**
     * Construct a new case-sensitive wildcard filter for a single wildcard.
     *
     * @param wildcard  the wildcard to match
     * @throws IllegalArgumentException if the pattern is null
     */
    public WildcardClassFilter() {
        this(Case.SENSITIVE);
    }
    
    /**
     * Construct a new case-sensitive wildcard filter for a single wildcard.
     *
     * @param wildcard  the wildcard to match
     * @throws IllegalArgumentException if the pattern is null
     */
    public WildcardClassFilter(Case caseSensitivity) {
        super();
        this.caseSensitivity = caseSensitivity == null ? Case.SENSITIVE : caseSensitivity;
        this.wildcards = new ArrayList<String>();
    }
    /**
     * Construct a new case-sensitive wildcard filter for a single wildcard.
     *
     * @param wildcard  the wildcard to match
     * @throws IllegalArgumentException if the pattern is null
     */
    public WildcardClassFilter(String wildcard) {
        this(wildcard, null);
    }

    /**
     * Construct a new wildcard filter for a single wildcard specifying case-sensitivity.
     *
     * @param wildcard  the wildcard to match, not null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the pattern is null
     */
    public WildcardClassFilter(String wildcard, Case caseSensitivity) {
    	this(caseSensitivity);
    	addWildcard( wildcard );
    }

    /**
     * Construct a new case-sensitive wildcard filter for an array of wildcards.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     *
     * @param wildcards  the array of wildcards to match
     * @throws IllegalArgumentException if the pattern array is null
     */
    public WildcardClassFilter(String[] wildcards) {
        this(wildcards, null);
    }

    /**
     * Construct a new wildcard filter for an array of wildcards specifying case-sensitivity.
     * <p>
     * The array is not cloned, so could be changed after constructing the
     * instance. This would be inadvisable however.
     *
     * @param wildcards  the array of wildcards to match, not null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the pattern array is null
     */
    public WildcardClassFilter(String[] wildcards, Case caseSensitivity) {
    	this(caseSensitivity);
    	if (wildcards == null) {
            throw new IllegalArgumentException("The wildcard array must not be null");
        }
    	for (String s : wildcards )
    	{
    		addWildcard( s );
    	}
    }

    /**
     * Construct a new case-sensitive wildcard filter for a list of wildcards.
     *
     * @param wildcards  the list of wildcards to match, not null
     * @throws IllegalArgumentException if the pattern list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public WildcardClassFilter(List<String> wildcards) {
        this(wildcards, null);
    }

    /**
     * Construct a new wildcard filter for a list of wildcards specifying case-sensitivity.
     *
     * @param wildcards  the list of wildcards to match, not null
     * @param caseSensitivity  how to handle case sensitivity, null means case-sensitive
     * @throws IllegalArgumentException if the pattern list is null
     * @throws ClassCastException if the list does not contain Strings
     */
    public WildcardClassFilter(List<String> wildcards, Case caseSensitivity) {
    	this(caseSensitivity);
    	if (wildcards == null) {
            throw new IllegalArgumentException("The wildcard array must not be null");
        }
    	for (String s : wildcards )
    	{
    		addWildcard( s );
    	}
    }
    
    public void addWildcard(String wildcard)
    {
    	addClassFilter( new RegexClassFilter( makeRegex( wildcard ), 
    			caseSensitivity));
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
        if (wildcards != null) {
            for (int i = 0; i < wildcards.size(); i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(wildcards.get(i));
            }
        }
        buffer.append(")");
        return buffer.toString();
    }
    
    private static void parseWildBlock( StringBuilder sb, String s)
    {
    	String[] blocks = s.split( "\\*" );
    	Iterator<String> iter = Arrays.asList(blocks).iterator();
    	sb.append( Matcher.quoteReplacement(iter.next()) );
    	while (iter.hasNext() )
    	{
    		sb.append( ".*" ).append(Matcher.quoteReplacement(iter.next()));
    	}
    }
    
    private static void parseWildChar(StringBuilder sb, String s)
    {
    	String[] blocks = s.split( "\\?" );
    	Iterator<String> iter = Arrays.asList(blocks).iterator();
    	parseWildBlock(sb, iter.next());
    	while (iter.hasNext() )
    	{
    		sb.append( "." );
    		parseWildBlock( sb, iter.next());
    	}
    }
    
    private static StringBuilder parseDot(StringBuilder sb, String s)
    {
    	String[] blocks = s.split( "\\." );
    	Iterator<String> iter = Arrays.asList(blocks).iterator();
    	parseWildBlock(sb, iter.next());
    	while (iter.hasNext() )
    	{
    		sb.append( "\\." );
    		parseWildChar( sb, iter.next());
    	}
    	return sb;
    }
    
    private static String makeRegex( String wildcard ) {
    	if (wildcard == null) {
            throw new IllegalArgumentException("The wildcard must not be null");
        }
    	return parseDot( new StringBuilder("^"), wildcard ).append("$").toString();
    }
    
}
