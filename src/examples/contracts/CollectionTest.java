/*
 * This code provided as an example and is not to be considered complete or production ready.
 */
package contracts;

import static org.junit.Assert.*;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

@Contract(Collection.class)
public class CollectionTest {

	private IProducer<Collection<Object>> producer;
	private Collection<Object> c;

	@Contract.Inject
	public void setCollection(IProducer<Collection<Object>> producer) {
		this.producer = producer;
	}

	@Before
	public void populate() {
		c = producer.newInstance();
	}

	@After
	public void cleanup() {
		producer.cleanUp();
	}

	@ContractTest
	public void empty() throws Exception {
		assertTrue(c.isEmpty());
	}

	@ContractTest
	public void size() throws Exception {
		assertEquals(0, c.size());
	}
}
