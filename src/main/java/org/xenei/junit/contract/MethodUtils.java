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
