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
import java.util.List;

/**
 * Interface that defines a ClassFilter.
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
	 * 
	 * @param clazz
	 *            the class to accept.
	 * @return True if the class matches the filter, false otherwise.
	 */
	boolean accept(Class<?> clazz);

	/**
	 * Accept a class name. In some cases this is a string compare in other
	 * cases the class is loaded from the class loader and other comparisons
	 * made.
	 * 
	 * @param className
	 *            the class name to accept.
	 * @return True if the class matches the filter, false otherwise.
	 */
	boolean accept(String className);

	/**
	 * Function name for the filter. Used in parsing filter constructs from
	 * strings.
	 * 
	 * @return The function name.
	 */
	String funcName();

	/**
	 * Filter the collection. Results will be returned in the order that the
	 * default iterator will return them from the collections parameter.
	 * 
	 * @param collection
	 *            the collection of classes to filter.
	 * @return The filtered collection
	 */
	Collection<Class<?>> filter(Collection<Class<?>> collection);

	/**
	 * Filter the collection. Results will be returned in the order that the
	 * default iterator will return them from the collections parameter.
	 * 
	 * @param collection
	 *            the collection of class names to filter.
	 * @return The filtered collection
	 */
	Collection<String> filterNames(Collection<String> collection);

	/**
	 * Get the arguments for the function.
	 * 
	 * @return the arguments for this function.
	 */
	String[] args();

	/**
	 * A collection of utility functions of ClassFilters.
	 *
	 */
	public static class Util {

		/**
		 * filter a collection of classes with the filter.
		 * 
		 * @param classes
		 *            The collection of classes to filter.
		 * @param filter
		 *            The filter to apply.
		 * @return A collection of classes that pass the filter in the order the
		 *         classes iterator returns them.
		 */
		public static Collection<Class<?>> filterClasses(
				Collection<Class<?>> classes, ClassFilter filter) {
			Collection<Class<?>> retval = new ArrayList<Class<?>>();
			for (Class<?> clazz : classes) {
				if (filter.accept(clazz)) {
					retval.add(clazz);
				}
			}
			return retval;
		}

		/**
		 * filter a collection of class names with the filter.
		 * 
		 * @param classNames
		 *            The collection of class names to filter.
		 * @param filter
		 *            The filter to apply.
		 * @return A collection of classes that pass the filter in the order the
		 *         classNames iterator returns them.
		 */
		public static Collection<String> filterClassNames(
				Collection<String> classNames, ClassFilter filter) {
			Collection<String> retval = new ArrayList<String>();
			for (String className : classNames) {
				if (filter.accept(className)) {
					retval.add(className);
				}
			}
			return retval;
		}

		/**
		 * Convert a ClassFilter to a string in a way that the Parser.parse()
		 * can parse it. Recommended for ClassFilter.toString() implementation.
		 * 
		 * @param filter
		 *            The filter to create the string for.
		 * @return The string.
		 */
		public static String toString(ClassFilter filter) {
			StringBuilder sb = new StringBuilder(filter.funcName()).append("(");
			String[] args = filter.args();
			if (args.length > 0) {
				sb.append(" ");

				for (int i = 0; i < args.length; i++) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append(args[i]);
				}
				sb.append(" ");
			}
			return sb.append(")").toString();
		}

		/**
		 * Parse the arguments for a filter.
		 * 
		 * @param str
		 *            The string to parse.
		 * @param pos
		 *            the offset within the string of the opening paren '(' for
		 *            the arguments.
		 * @return The string comprising all the characters between the opening
		 *         paren and its matching close paren.
		 * @throws IllegalArgumentException
		 *             if the string can not be parsed.
		 */
		public static String parseArgs(String str, int pos) {
			int cnt = 1;
			for (int i = pos + 1; i < str.length(); i++) {
				switch (str.charAt(i)) {
				case '(':
					cnt++;
					break;
				case ')':
					cnt--;
					if (cnt == 0) {
						return str.substring(pos, i - 1);
					}
					break;
				case ',':

				default:
					// do nothing;
					break;
				}
			}
			throw new IllegalArgumentException("Can not parse " + str);
		}
	}
}
