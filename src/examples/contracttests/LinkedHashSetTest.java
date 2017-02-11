/*
 * This code provided as an example and is not to be considered complete or production ready.
 */
package contracttests;

import java.util.LinkedHashSet;
import org.junit.runner.RunWith;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

@RunWith(ContractSuite.class)
@ContractImpl(LinkedHashSet.class)
public class LinkedHashSetTest {

	IProducer<LinkedHashSet<Object>> producer = new IProducer<LinkedHashSet<Object>>() {
		public LinkedHashSet<Object> newInstance() {
			return new LinkedHashSet<Object>();
		}

		public void cleanUp() {
			// no cleanup required.
		}
	};

	@Contract.Inject
	public IProducer<LinkedHashSet<Object>> makedashSet() {
		return producer;
	}
}
