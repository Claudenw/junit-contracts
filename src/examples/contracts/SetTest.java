/*
 * This code provided as an example and is not to be considered complete or production ready.
 */
package contracts;

import static org.junit.Assert.*;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

@Contract(Set.class)
public class SetTest {

	private IProducer<Set<Object>> producer;
	private Set<Object> c;

	@Contract.Inject
	public void setSet(IProducer<Set<Object>> producer) {
		this.producer = producer;

	}

	@Before
	public void populate() {
		c = producer.newInstance();
		c.add("Hello");
	}

	@After
	public void cleanup() {
		producer.cleanUp();
	}

	@ContractTest
	public void contains() throws Exception {
		assertTrue(c.contains(("Hello")));
		assertFalse(c.contains("World"));
	}

	@ContractTest
	public void add() throws Exception {
		c.add("World");
		assertTrue(c.contains("World"));
	}
}
