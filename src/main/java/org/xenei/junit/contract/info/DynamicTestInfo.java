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

	public DynamicTestInfo(Class<?> cls, ContractImpl impl,
			DynamicSuiteInfo suite) {
		super(cls, impl, MethodUtils.findAnnotatedSetter(cls,
				Contract.Inject.class));
		if (this.getMethod() == null) {
			throw new IllegalStateException(
					"Dynamic tests annotated with @RunWith(ContractSuite.class) ("
							+ getTestClass()
							+ ") must include a @Contract.Inject annotation on a concrete declared setter method");

		}
		getter = MethodUtils.findAnnotatedGetter(cls, Contract.Inject.class);
		if (getter == null) {
			throw new IllegalStateException(
					"Dynamic tests annotated with @RunWith(ContractSuite.class) ("
							+ getTestClass()
							+ ") must include a @Contract.Inject annotation on a concrete declared getter method");

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
	public Object getProducer(Object baseProducer)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException {
		Object suiteTest = getTestClass().newInstance();
		getMethod().invoke(suiteTest, baseProducer);
		return getter.invoke(suiteTest);
	}
}