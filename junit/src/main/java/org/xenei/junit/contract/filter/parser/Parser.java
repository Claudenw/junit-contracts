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

package org.xenei.junit.contract.filter.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xenei.junit.contract.filter.AbstractClassFilter;
import org.xenei.junit.contract.filter.AbstractStringClassFilter;
import org.xenei.junit.contract.filter.AndClassFilter;
import org.xenei.junit.contract.filter.AnnotationClassFilter;
import org.xenei.junit.contract.filter.Case;
import org.xenei.junit.contract.filter.ClassFilter;
import org.xenei.junit.contract.filter.ConditionalClassFilter;
import org.xenei.junit.contract.filter.FalseClassFilter;
import org.xenei.junit.contract.filter.HasAnnotationClassFilter;
import org.xenei.junit.contract.filter.InterfaceClassFilter;
import org.xenei.junit.contract.filter.NameClassFilter;
import org.xenei.junit.contract.filter.NotClassFilter;
import org.xenei.junit.contract.filter.OrClassFilter;
import org.xenei.junit.contract.filter.PrefixClassFilter;
import org.xenei.junit.contract.filter.RegexClassFilter;
import org.xenei.junit.contract.filter.SuffixClassFilter;
import org.xenei.junit.contract.filter.TrueClassFilter;
import org.xenei.junit.contract.filter.WildcardClassFilter;

/**
 * Class to parse a function string into a ClassFilter.
 *
 */
public class Parser {
	private Map<String, Class<? extends ClassFilter>> map = new HashMap<String, Class<? extends ClassFilter>>();

	/**
	 * Constructor
	 */
	public Parser() {
		map.put("abstract", AbstractClassFilter.class);
		map.put("and", AndClassFilter.class);
		map.put("annotation", AnnotationClassFilter.class);
		map.put("false", FalseClassFilter.class);
		map.put("hasannotation", HasAnnotationClassFilter.class);
		map.put("interface", InterfaceClassFilter.class);
		map.put("name", NameClassFilter.class);
		map.put("not", NotClassFilter.class);
		map.put("or", OrClassFilter.class);
		map.put("prefix", PrefixClassFilter.class);
		map.put("regex", RegexClassFilter.class);
		map.put("suffix", SuffixClassFilter.class);
		map.put("true", TrueClassFilter.class);
		map.put("wildcard", WildcardClassFilter.class);
	}

	/**
	 * Parses the class filter string into a ClassFilter object. 
	 * <p>
	 * Strings are of
	 * the form:
	 * 
	 * <pre>
	 * FilterName( arg[,arg[,arg[,...]]] )
	 * </pre>
	 * 
	 * Where FilterName is the name of the desired filter class without the
	 * "ClassFilter" ending. (e.g. True() for the class TrueClassFilter).
	 * 
	 * The result is a prefix notation function. For example:
	 * 
	 * <pre>
	 * Or(Interface(), Not(Prefix(org.xenei)))
	 * </pre>
	 * 
	 * is a function that accepts classes that are either interfaces or whos
	 * fully qualified java name does not start with "org.xenei" 
	 * </p>
	 * <p>
	 * Filters that accept a Case as the first argument in the constructor may have the case
	 * specified as the first parameter as either <code>Sensitive</code> or <code>Insensitive</code>.
	 * </p>
	 * 
	 * @param filterStr
	 * @return The ClassFilter
	 * @throws IllegalArgumentException
	 *             If the filter can not be build.
	 */
	public ClassFilter parse(String filterStr) throws IllegalArgumentException {
		ParserInfo info = new ParserInfo(filterStr);

		String args = info.parseArgs();
		try {
			if (args.length() == 0) {
				try {
					return (ClassFilter) ClassFilter.class.getDeclaredField(
							info.name.toUpperCase()).get(null);
				} catch (NoSuchFieldException e) {
					return info.clazz.getConstructor().newInstance();
				}
			}

			if (ConditionalClassFilter.class.isAssignableFrom(info.clazz)) {
				ConditionalClassFilter filter;

				filter = (ConditionalClassFilter) info.clazz.newInstance();

				// parse the functions.
				int cnt = 0;
				int startPos = 0;
				boolean scanning = false;
				for (int i = 0; i < args.length(); i++) {
					switch (args.charAt(i)) {
					case '(':
						cnt++;
						break;
					case ')':
						cnt--;
						if (cnt == 0) {
							filter.addClassFilter(parse(args.substring(
									startPos, i + 1)));
							scanning = true;
						}
						break;
					case ',':
						if (scanning) {
							startPos = i + 1;
							break;
						}
					default:
						// do nothing;
						break;
					}
				}
				return filter;
			}

			String[] argStrAry = args.split(",");
			if (AbstractStringClassFilter.class.isAssignableFrom(info.clazz)) {
				AbstractStringClassFilter filter = null;
				Case caze = null;
				try {
					caze = Case.forName(argStrAry[0].trim());
				} catch (IllegalArgumentException expected) {
					// this catch has to be here because later
					// IllegalArgumenExceptions must be thrown.

					filter = (AbstractStringClassFilter) info.clazz
							.getConstructor(String.class).newInstance(
									argStrAry[0].trim());

					for (int i = 1; i < argStrAry.length; i++) {
						filter.addString(argStrAry[i].trim());
					}
					return filter;
				}
				if (argStrAry.length < 2) {
					throw new IllegalArgumentException(
							String.format(" Not enough arguments for %s: %s",
									info.name, args));
				}
				filter = (AbstractStringClassFilter) info.clazz.getConstructor(
						Case.class, String.class).newInstance(caze,
						argStrAry[1].trim());
				for (int i = 2; i < argStrAry.length; i++) {
					filter.addString(argStrAry[i].trim());
				}

				return filter;
			}

			if (WildcardClassFilter.class.isAssignableFrom(info.clazz)) {
				WildcardClassFilter filter = new WildcardClassFilter(
						Case.forName(argStrAry[0].trim()));
				for (int i = 1; i < argStrAry.length; i++) {
					filter.addWildcard(argStrAry[i].trim());
				}
				return filter;
			}

			if (HasAnnotationClassFilter.class.isAssignableFrom(info.clazz)) {
				if (argStrAry.length != 1) {
					throw new IllegalArgumentException(
							String.format(
									"More than one string provided for HasAnnotation: %s",
									args));
				}
				Class<?> cls;
				try {
					cls = Thread.currentThread().getContextClassLoader()
							.loadClass(args.trim());
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException(
							String.format("Error creating: %s: %s", info.name,
									e.getMessage()), e);
				}
				if (Annotation.class.isAssignableFrom(cls)) {
					return new HasAnnotationClassFilter(
							(Class<? extends Annotation>) cls);
				}
				throw new IllegalArgumentException(String.format(
						"%s is not an Annotation", args));
			}

			if (RegexClassFilter.class.isAssignableFrom(info.clazz)) {
				if (argStrAry.length == 1) {
					return new RegexClassFilter(args.trim());
				}

				if (argStrAry.length == 2) {
					return new RegexClassFilter(Case.forName(argStrAry[0]
							.trim()), argStrAry[1].trim());
				}
			}

			if (NotClassFilter.class.isAssignableFrom(info.clazz)) {
				return new NotClassFilter(parse(args));
			}
			throw new IllegalArgumentException("Unrecognized filter: "
					+ info.name);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(
					String.format("Unable to instantiate: %s: %s", info.name,
							e.getMessage()), e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(String.format(
					"Security exception instantiating: %s: %s", info.name,
					e.getMessage()), e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.format(
					"Error instantiating: %s: %s", info.name, e.getMessage()),
					e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(String.format(
					"Error instantiating: %s: %s", info.name, e.getMessage()),
					e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(String.format(
					"Can not find constructor for: %s: %s", info.name,
					e.getMessage()), e);

		}

	}

	/**
	 * Class that holds information about a single function parse.
	 *
	 */
	private final class ParserInfo {
		private String parserStr;
		private String name;
		private Class<? extends ClassFilter> clazz;
		private List<?> args;

		ParserInfo(String parserStr) {
			this.parserStr = parserStr.trim();
			int pos = this.parserStr.indexOf('(');
			name = this.parserStr.substring(0, pos);
			clazz = map.get(name.toLowerCase());
			if (clazz == null) {
				throw new IllegalArgumentException(name
						+ " is not a registered class filter");
			}
		}

		public String parseArgs() {
			int cnt = 1;
			for (int i = name.length() + 1; i < parserStr.length(); i++) {
				switch (parserStr.charAt(i)) {
				case '(':
					cnt++;
					break;
				case ')':
					cnt--;
					if (cnt == 0) {
						if (name.length() == i - 1) {
							return "";
						}
						return parserStr.substring(name.length() + 1, i - 1);
					}
					break;
				default:
					// do nothing;
					break;
				}
			}
			throw new IllegalStateException("Can not parse " + parserStr);
		}

	}
	
}
