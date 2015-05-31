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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Interface that defines a ClassFilter.=
 *
 */
public interface ClassFilter {
	public static final ClassFilter TRUE = TrueClassFilter.TRUE;
	public static final ClassFilter FALSE = FalseClassFilter.FALSE;
	public static final ClassFilter ANNOTATION = AnnotationClassFilter.ANNOTATION;
	public static final ClassFilter ABSTRACT = AbstractClassFilter.ABSTRACT;
	public static final ClassFilter INTERFACE = InterfaceClassFilter.INTERFACE;
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
	
	/**
	 * Function name for the filter.  Used in parsing filter constructs from strings.
	 * @return The function name.
	 */
	String funcName();
	
	/**
	 * Filter the collection.
	 * Results will be returned in the order that the default iterator will return them
	 * from the collections parameter.
	 * @param collection the collection of classes to filter.
	 * @return The filtered collection
	 */
	Collection<Class<?>> filter( Collection<Class<?>> collection);
	
	/**
	 * Filter the collection.
	 * Results will be returned in the order that the default iterator will return them
	 * from the collections parameter.
	 * @param collection the collection of class names to filter.
	 * @return The filtered collection
	 */
	Collection<String> filterNames( Collection<String> collection);
	
	/**
	 * The name 
	 * @return the arguments for this function.
	 */
	String[] args();
	
	public static class Util {
		
		public static Collection<Class<?>> filterClasses( Collection<Class<?>> classes,ClassFilter filter)
		{
			Collection<Class<?>> retval = new ArrayList<Class<?>>();
			for (Class<?> clazz : classes)
			{
				if (filter.accept(clazz))
				{
					retval.add( clazz );
				}
			}
			return retval;
		}
		
		public static Collection<String> filterClassNames( Collection<String> classNames, ClassFilter filter)
		{
			Collection<String> retval = new ArrayList<String>();
			for (String className : classNames)
			{
				if (filter.accept(className))
				{
					retval.add( className );
				}
			}
			return retval;
		}
		
	    public static String toString(ClassFilter filter) {
	        StringBuilder sb = new StringBuilder( filter.funcName() ).append( "(");
	        String[] args = filter.args();
	        if (args.length>0)
	        {
	        	sb.append(" ");
	        
		        for (int i=0;i<args.length;i++)
		        {
		        	if (i>0)
		        	{
		        		sb.append( ", ");
		        	}
		        	sb.append( args[i] );
		        }
	        	sb.append( " ");
	        }
	        return sb.append(")").toString();
	    }  
	
	    
		public static String parseArgs( String s, int pos )
		{		
			int cnt = 1;
			List<String> lst = new ArrayList<String>();
			for (int i=pos+1;i<s.length();i++)
			{
				switch (s.charAt(i)) 
				{
					case '(' :
						cnt++;
						break;
					case ')' :
						cnt--;
						if (cnt==0)
						{
							return s.substring( pos, i-1);
						}
						break;
					case ',' :
						
					default:
						// do nothing;
						break;
				}
			}
			throw new IllegalStateException( "Can not parse "+s);
		}
	}
}
