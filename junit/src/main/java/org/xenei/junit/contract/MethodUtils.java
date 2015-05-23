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

package org.xenei.junit.contract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Static methods to find annotated getter and setters.
 * 
 */
public class MethodUtils {

	/**
	 * Find a getter with the specified annotation. getter must be annotated,
	 * return a value, not be abstract and not take any parameters and is
	 * public.
	 * 
	 * @param cls
	 *            Class that declares the method to find.
	 * @param class1
	 *            the annotation to find.
	 * @return getter method or null
	 */
	public static Method findAnnotatedGetter(Class<?> cls,
			Class<? extends Annotation> class1) {
		for (Method m : cls.getDeclaredMethods()) {
			if (m.getAnnotation(class1) != null) {
				if (!m.getReturnType().equals(Void.TYPE)
						&& !Modifier.isAbstract(m.getModifiers())
						&& (m.getParameterTypes().length == 0)
						// must be public
						&& (Modifier.isPublic(m.getModifiers()))) {
					return m;
				}
			}
		}
		return null;
	}

	/**
	 * Find a setter with the specified annotation. getter must be annotated,
	 * not return a value, take one parameter not be abstract and is public.
	 * 
	 * @param cls
	 *            Class that declares the method to find.
	 * @param class1
	 *            the annotation to find.
	 * @return setter method or null
	 */
	public static Method findAnnotatedSetter(Class<?> cls,
			Class<? extends Annotation> class1) {
		for (Method m : cls.getDeclaredMethods()) {
			// method is annotated
			if ((m.getAnnotation(class1) != null)
			// method does not return a value
					&& m.getReturnType().equals(Void.TYPE)
					// method is not abstract
					&& !Modifier.isAbstract(m.getModifiers())
					// method has one argument
					&& (m.getParameterTypes().length == 1)
					// must be public
					&& (Modifier.isPublic(m.getModifiers()))) {
				return m;
			}
		}
		return null;
	}
}
