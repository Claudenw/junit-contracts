/*
 * This code provided as an example and is not to be considered complete or production ready.
 */
package contracts;

import static org.junit.Assert.*;
import java.util.SortedSet;

import org.junit.After;
import org.junit.Before;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

@Contract(SortedSet.class)
public class SortedSetTest {

	private IProducer<SortedSet<Object>> producer;
	private SortedSet<Object> c;

	@Contract.Inject
	public void setSet(IProducer<SortedSet<Object>> producer) {
		this.producer = producer;
	}

	@Before
	public void populate() {
		c = producer.newInstance();
		// Deliberately not added in order
		c.add("c");
		c.add("a");
		c.add("b");
	}

	@After
	public void cleanup() {
		producer.cleanUp();
	}

	@ContractTest
	public void first() throws Exception {
		assertEquals("a", c.first());
	     assertEquals("c", c.last());
    }
}
