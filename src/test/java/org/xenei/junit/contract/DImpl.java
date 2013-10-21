package org.xenei.junit.contract;

/**
 * concrete implementation of D
 * 
 */
class DImpl implements D {

	@Override
	public String getDName() {
		return "dname";
	}

	@Override
	public A getA() {
		return new AImpl();
	}

	@Override
	public B getB() {
		return new BImpl();
	}

}
