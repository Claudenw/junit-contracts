package org.xenei.junit.contract.info;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.MethodUtils;

/**
 * Handles tests in dynamic suites.
 *
 * When executing a dynamic suite test the dynamic.inject method will be called
 * to retrieve the instance to inject then the Contract.inject should be called
 * to inject it into the test.
 *
 */
public class DynamicTestInfo extends TestInfo {
	private final Method dynamicInjector;
	private final Method getter;

	public DynamicTestInfo(final Class<?> testClass, final ContractImpl impl,
			final DynamicSuiteInfo suite) {
		super(testClass, impl, MethodUtils.findAnnotatedSetter(testClass,
				Contract.Inject.class));
		if (this.getMethod() == null) {
			addError(new IllegalStateException(
					"Dynamic tests annotated with @RunWith(ContractSuite.class) ("
							+ getContractTestClass()
							+ ") must include a @Contract.Inject annotation on a concrete declared setter method"));

		}
		getter = MethodUtils.findAnnotatedGetter(testClass,
				Contract.Inject.class);
		if (getter == null) {
			addError(new IllegalStateException(
					"Dynamic tests annotated with @RunWith(ContractSuite.class) ("
							+ getContractTestClass()
							+ ") must include a @Contract.Inject annotation on a concrete declared getter method"));

		}
		dynamicInjector = suite.getDynamicInjector();
	}

	public Method getDynamicInjector() {
		return dynamicInjector;
	}

	/**
	 * Get a producer that is to be injected in to the test.
	 *
	 * @param baseProducer
	 *            the producer for the suite as designated by the Dynamic.Inject
	 *            annotation
	 * @return The producer object
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public Object getProducer(final Object baseProducer)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException {
		final Object suiteTest = getContractTestClass().newInstance();
		getMethod().invoke(suiteTest, baseProducer);
		return getter.invoke(suiteTest);
	}
}