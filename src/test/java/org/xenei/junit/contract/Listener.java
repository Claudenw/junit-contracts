package org.xenei.junit.contract;

import java.util.ArrayList;
import java.util.List;

public class Listener {

	private static final ThreadLocal<List<String>> history = new ThreadLocal<List<String>>() {
		@Override
		protected List<String> initialValue() {
			return new ArrayList<String>();
		}
	};

	public static void add(String o) {
		System.out.println(o);
		List<String> l = history.get();
		l.add(o);
		history.set(l);
	}

	public static List<String> get() {
		return history.get();
	}

	public static void clear() {
		history.set(new ArrayList<String>());
	}

}
