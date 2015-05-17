package org.xenei.junit.bad;
/**
 * Class to test that the @Ignore annotation works
 */
import org.junit.Ignore;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.IProducer;
import org.xenei.junit.contract.exampleTests.A;

@Contract(A.class)
@Ignore
public abstract class BadAbstract {
	
	public BadAbstract() {
	}
	
	@Contract.Inject
	public final void setProducer(IProducer<A> producer) {}

}
