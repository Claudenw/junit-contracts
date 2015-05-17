package org.xenei.junit.contract.info;

import java.lang.reflect.Method;

import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.MethodUtils;

/**
 * Class that contains the contract test and the class that is the contract as
 * well as the method used to get the producer implementation for the suite
 * tests.
 *
 */
public class SuiteInfo extends TestInfo {

	/**
	 * Constructor
	 *
	 * @param testSuite
	 *            the test suite definition class.
	 */
	public SuiteInfo(final Class<?> testSuite, final ContractImpl impl) {
		super(testSuite, impl, MethodUtils.findAnnotatedGetter(testSuite,
				Contract.Inject.class));
		if (this.getMethod() == null) {
			addError(new IllegalStateException(
					"Classes annotated with @RunWith(ContractSuite.class) ("
							+ getContractTestClass()
							+ ") must include a @Contract.Inject annotation on a concrete declared getter method"));
		}
	}

	protected SuiteInfo(final Class<?> testSuite, final ContractImpl impl,
			final Method m) {
		super(testSuite, impl, m);
	}
}