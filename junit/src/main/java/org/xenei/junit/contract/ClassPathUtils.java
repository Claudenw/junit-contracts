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

package org.xenei.junit.contract;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Package of class path searching utilities
 *
 */
public class ClassPathUtils {

	private static final Logger LOG = LoggerFactory
			.getLogger(ClassPathUtils.class);

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs. Adapted from http://snippets.dzone.com/posts/show/4831 and
	 * extended to support use of JAR files
	 *
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws IOException
	 */
	public static Set<String> findClasses(final String directory,
			final String packageName) throws IOException {
		final Set<String> classes = new HashSet<String>();
		if (directory.startsWith("file:") && directory.contains("!")) {
			final String[] split = directory.split("!");
			final URL jar = new URL(split[0]);
			final ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry entry = null;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					final String className = entry.getName()
							.replaceAll("\\$.*", "").replaceAll("\\.class", "")
							.replace('/', '.');
					if (className.startsWith(packageName)) {
						classes.add(className);
					}
				}
			}
		}
		else {
			final File dir = new File(directory);
			if (!dir.exists()) {
				return classes;
			}
			final File[] files = dir.listFiles();
			for (final File file : files) {
				if (file.isDirectory()) {
					/* META-INF includes directories with dots in the name. So
					 * we will just ignore them because there may be other cases where
					 * we need to skip them we will not just skip the META-INF dir.
					 */
					if (!file.getName().contains(".")) {
						final String newPkgName = String.format("%s%s%s",
								packageName, (packageName.length() > 0 ? "."
										: ""), file.getName());
						classes.addAll(findClasses(file.getAbsolutePath(),
								newPkgName));
					}
				}
				else if (file.getName().endsWith(".class")) {
					classes.add(packageName
							+ '.'
							+ file.getName().substring(0,
									file.getName().length() - 6));
				}
			}
		}
		return classes;
	}

	/**
	 * Find all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * An empty or null packageName = all packages.
	 *
	 * @param packageName
	 *            The base package or class name.
	 * @return The classes
	 */
	public static Collection<Class<?>> getClasses(final String packageName) {
		final ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			LOG.error("Class loader may not be null.  No class loader for current thread");
			return Collections.emptyList();
		}
		return getClasses(classLoader, packageName);
	}

	/**
	 * Find all classes accessible from the classloader which belong to the
	 * given package and subpackages.
	 *
	 * Adapted from http://snippets.dzone.com/posts/show/4831 and extended to
	 * support use of JAR files
	 *
	 * @param classLoader
	 *            The classloader to load the classes from.
	 * @param packageName
	 *            The base package or class name
	 * @return The classes
	 */
	public static Collection<Class<?>> getClasses(
			final ClassLoader classLoader, final String packageName) {
		if (classLoader == null) {
			LOG.error("Class loader may not be null.");
			return Collections.emptyList();
		}
		final String path = packageName == null ? "" : packageName.replace('.',
				'/');
		Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(path);
		} catch (final IOException e1) {
			LOG.error(e1.toString());
			return Collections.emptyList();
		}
		final Set<Class<?>> classes = new HashSet<Class<?>>();
		if (resources.hasMoreElements()) {
			while (resources.hasMoreElements()) {
				final URL resource = resources.nextElement();
				try {
					for (final String clazz : findClasses(resource.getFile(),
							packageName)) {
						try {
							LOG.debug("Adding class {}", clazz);
							classes.add(Class
									.forName(clazz, false, classLoader));
						} catch (final ClassNotFoundException e) {
							LOG.warn(e.toString());
						}
					}
				} catch (final IOException e) {
					LOG.warn(e.toString());
				}
			}
		}
		else {
			// there are no resources at that path so see if it is a class
			try {
				classes.add(Class.forName(packageName));
			} catch (final ClassNotFoundException e) {
				LOG.warn(String.format(
						"'%s' was neither a package name nor a class name",
						packageName));
			}
		}
		return classes;
	}

	/**
	 * Get the array of class path elements.
	 *
	 * These are strings separated by java.class.path property
	 *
	 * @return Array of class path elements
	 */
	public static String[] getClassPathElements() {
		final String splitter = String.format("\\%s",
				System.getProperty("path.separator"));
		final String[] classPath = System.getProperty("java.class.path").split(
				splitter);
		return classPath;
	}

	/**
	 * Get all the interfaces for the class.
	 *
	 * @param clazz
	 *            The class to find interfaces for.
	 * @return set of interfaces implemented by clazz.
	 */
	public static Set<Class<?>> getAllInterfaces(final Class<?> clazz) {
		// set of implementation classes
		final Set<Class<?>> implClasses = new LinkedHashSet<Class<?>>();
		// populate the set of implementation classes
		ClassPathUtils.getAllInterfaces(implClasses, clazz);
		return implClasses;
	}

	/**
	 * Get all the interfaces that the class implements. Adds the interfaces to
	 * the set of classes.
	 *
	 * This method calls recursively to find all parent interfaces.
	 *
	 * @param set
	 *            The set off classes to add the interface classes to.
	 * @param c
	 *            The class to check.
	 */
	public static void getAllInterfaces(final Set<Class<?>> set,
			final Class<?> c) {
		if ((c == null) || (c == Object.class)) {
			return;
		}
		for (final Class<?> i : c.getClasses()) {
			if (i.isInterface()) {
				if (!set.contains(i)) {
					set.add(i);
					getAllInterfaces(set, i);
				}
			}
		}
		for (final Class<?> i : c.getInterfaces()) {
			if (!set.contains(i)) {
				set.add(i);
				getAllInterfaces(set, i);
			}
		}
		getAllInterfaces(set, c.getSuperclass());
	}

}
