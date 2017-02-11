/*
 * This code provided as an example and is not to be considered complete or production ready.
 */
package contracttests;

import java.util.HashSet;
import org.junit.runner.RunWith;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

@RunWith(ContractSuite.class)
@ContractImpl(HashSet.class)
public class HashSetTest {

	IProducer<HashSet<Object>> producer = new IProducer<HashSet<Object>>() {
		public HashSet<Object> newInstance() {
			return new HashSet<Object>();
		}

		public void cleanUp() {
			// no cleanup required.
		}
	};

	@Contract.Inject
	public IProducer<HashSet<Object>> makedashSet() {
		return producer;
	}
}