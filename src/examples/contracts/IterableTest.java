/*
 * This code provided as an example and is not to be considered complete or production ready.
 */
package contracts;

import static org.junit.Assert.*;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractTest;
import org.xenei.junit.contract.IProducer;

@Contract(Iterable.class)
public class IterableTest {
	private IProducer<Iterable<Object>> producer;
	private Iterable<Object> it;

	@Contract.Inject
	public void setIterable(IProducer<Iterable<Object>> producer) {
		this.producer = producer;
	}

	@Before
	public void populate() {
		it = producer.newInstance();
	}

	@After
	public void cleanup() {
		producer.cleanUp();
	}

	@ContractTest
	public void hasNext() throws Exception {
		assertFalse(it.iterator().hasNext());
	}

	@ContractTest
	public void doubleIterator() throws Exception {
		it.iterator();
		it.iterator();
	}

	@ContractTest
	public void nextFails() throws Exception {
		try {
			it.iterator().next();
			fail("Didn't throw NoSuchElementException");
		} catch (NoSuchElementException ex) {
			// expected
		}
	}
}
