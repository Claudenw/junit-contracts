package org.xenei.junit.contract;

import java.lang.reflect.Method;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class ContractTestRunner extends BlockJUnit4ClassRunner {

	Method setter;
	Object getterObj;
	Method getter;
	public ContractTestRunner(Class<?> klass, Method setter, Object getterObj, Method getter) throws InitializationError {
		super(klass);
		this.setter = setter;
		this.getterObj = getterObj;
		this.getter = getter;
	}

	@Override
	protected Object createTest() throws Exception {
		Object o = getTestClass().getOnlyConstructor().newInstance();
		setter.invoke( o, getter.invoke( getterObj ));
		return o;
	}

}
