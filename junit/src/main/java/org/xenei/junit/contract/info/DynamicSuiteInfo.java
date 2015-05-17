package org.xenei.junit.contract.info;

import java.lang.reflect.Method;

import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.Dynamic;
import org.xenei.junit.contract.MethodUtils;

/**
 * Handles dynamic suites.
 *
 * When executing a dynamic suite the dynamic.inject method should be called to
 * retrieve the instance to inject then the Contract.inject should be called to
 * inject it into the test.
 *
 */
public class DynamicSuiteInfo extends SuiteInfo {
	private final Method dynamicInjector;

	/**
	 * Constructor
	 *
	 * @param dynamic
	 *            The class under test.
	 * @param impl
	 *            The ContractImpl annotation for the class
	 */
	public DynamicSuiteInfo(final Class<? extends Dynamic> dynamic,
			final ContractImpl impl) {
		super(dynamic, impl, MethodUtils.findAnnotatedGetter(impl.value(),
				Contract.Inject.class));
		dynamicInjector = MethodUtils.findAnnotatedGetter(dynamic,
				Dynamic.Inject.class);
		if (getMethod() == null) {
			addError(new IllegalArgumentException(
					"Classes that extends Dynamic ["
							+ dynamic
							+ "] must contain a getter method annotated with @Dynamic.Inject"));

		}
	}

	public Method getDynamicInjector() {
		return dynamicInjector;
	}
}