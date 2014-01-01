package org.xenei.junit.contract;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that records information so that the test cases can ensure that the
 * proper methods were called in the proper order.
 * <p>
 * This class creates a thread local version of itself so that the static
 * methods work in a thread safe way.
 * 
 */
public class Listener {

	private static final ThreadLocal<List<String>> history = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return new ArrayList<String>();
		}
	};

	/**
	 * Add a string to the list of strings.
	 * 
	 * <b>SIDE EFFECT</b> Prints the string to standard out.
	 * 
	 * @param o
	 *            the string to add
	 */
	public static void add(String o) {
		System.out.println(o);
		List<String> l = history.get();
		l.add(o);
		history.set(l);
	}

	/**
	 * Get the the list of strings.
	 * 
	 * @return
	 */
	public static List<String> get() {
		return history.get();
	}

	/**
	 * Clear the list of strings.
	 */
	public static void clear() {
		history.set(new ArrayList<String>());
	}

}
