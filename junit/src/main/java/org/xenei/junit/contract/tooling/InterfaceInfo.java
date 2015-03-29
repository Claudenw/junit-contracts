package org.xenei.junit.contract.tooling;

import java.util.HashSet;
import java.util.Set;

/**
 * The information about an interface.
 *
 * tracks the interface class and the classes that test the interface.
 *
 */
public class InterfaceInfo {
	// the interface class
	private final Class<?> name;
	// the tests that apply to the interface
	private final Set<Class<?>> tests;

	/**
	 * Constructor
	 */
	public InterfaceInfo(final Class<?> name) {
		this.name = name;
		this.tests = new HashSet<Class<?>>();
	}

	/**
	 * Add a test to the list of tests that apply to the interface.
	 * 
	 * @param test
	 *            The test to add.
	 */
	public void add(final Class<?> test) {
		tests.add(test);
	}

	/**
	 * Get the set of tests for the interface.
	 * 
	 * @return
	 */
	public Set<Class<?>> getTests() {
		return tests;
	}

	/**
	 * Get the interface class
	 * 
	 * @return The interface class.
	 */
	public Class<?> getName() {
		return name;
	}
}