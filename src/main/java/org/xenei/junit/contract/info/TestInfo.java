package org.xenei.junit.contract.info;

import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.MethodUtils;

/**
 * Class that contains the contract test and the class that is the contract as
 * well as the method used to get the producer implementation for the tests.
 * 
 */
public class TestInfo {
	// the test class
	private Class<?> contractTest;
	// the class under test
	private Class<?> contractClass;
	//
	private Class<?>[] skipTests;
	
	// the method to retrieve the producer implementation
	private Method method;

	/**
	 * Constructor
	 * 
	 * @param testSuite
	 *            The contract test this is part of
	 * @param impl
	 *            The class under test.
	 * @param m
	 *            The method to get the producer
	 */
	protected TestInfo(Class<?> testSuite, ContractImpl impl, Method m) {
		this.contractTest = testSuite;
		this.contractClass = impl.value();
		this.skipTests = impl.skip();
		this.method = m;
	}

	/**
	 * Constructor
	 * 
	 * @param contractTest
	 *            The contract under test.
	 * @param c
	 *            The Contract annotation for the contractTest
	 */
	public TestInfo(Class<?> contractTest, Contract c) {
		this.contractTest = contractTest;
		this.contractClass = c.value();
		this.skipTests = new Class<?>[0];
		this.method = MethodUtils.findAnnotatedSetter(
				contractTest, Contract.Inject.class);
		if (Modifier.isAbstract(contractTest.getModifiers())) {
			throw new IllegalStateException(
					"Classes annotated with @Contract (" + contractTest
							+ ") must not be abstract");
		}
		if (method == null) {
			throw new IllegalStateException(
					"Classes annotated with @Contract ("
							+ contractTest
							+ ") must include a @Contract.Inject annotation on a non-abstract declared setter method");
		}
	}

	/**
	 * Test contract test has a single constructor that takes parameter as an
	 * argument
	 */
	boolean hasInjection(Class<?> cls) {
		Constructor<?>[] constructors = contractTest.getConstructors();
		// not Foo NonStatic InnerClass()
		boolean retval = !(contractTest.isMemberClass() && !isStatic(contractTest
				.getModifiers()))
		// has one constructor
				&& (constructors.length == 1)
				// constructor has no argument
				&& (constructors[0].getParameterTypes().length == 0);
		return retval;
	}

	public Class<?>[] getSkipTests()
	{
		return skipTests;
	}
	
	public String getPackageName() {
		return contractClass.getPackage().getName();
	}

	public String getSimpleContractName() {
		return contractClass.getSimpleName();
	}

	public String getSimpleTestName() {
		return contractTest.getSimpleName();
	}

	public String getContractName() {
		return contractClass.getCanonicalName();
	}

	public String getTestName() {
		return contractTest.getCanonicalName();
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(contractTest.getModifiers());
	}

	public Class<?> getTestClass() {
		return contractTest;
	}

	public Class<?> getContractClass() {
		return contractClass;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return String.format("[%s testing %s]", getSimpleTestName(),
				getSimpleContractName());
	}
}