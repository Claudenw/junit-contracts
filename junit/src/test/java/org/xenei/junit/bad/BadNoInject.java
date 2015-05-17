package org.xenei.junit.bad;
/**
 * Class to test that the ignore option works.
 */
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.exampleTests.A;

@Contract(A.class)
public class BadNoInject {

	public BadNoInject() {
	}

}
