package org.xenei.junit.bad;

import org.xenei.junit.contract.A;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.IProducer;

@Contract(A.class)
public abstract class BadAbstrtact {
	
	public BadAbstrtact() {
	}
	
	@Contract.Inject
	public final void setProducer(IProducer<A> producer) {}

}
