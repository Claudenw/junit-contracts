package org.xenei.junit.contract.info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.xenei.classpathutils.filter.NameClassFilter;
import org.xenei.junit.bad.BadAbstract;
import org.xenei.junit.contract.exampleTests.A;
import org.xenei.junit.contract.exampleTests.AImpl;
import org.xenei.junit.contract.exampleTests.AT;
import org.xenei.junit.contract.exampleTests.B;
import org.xenei.junit.contract.exampleTests.C;
import org.xenei.junit.contract.exampleTests.CImpl;
import org.xenei.junit.contract.exampleTests.EImpl;
import org.junit.Assert;

/**
 * Tests for ContractTestMap
 *
 */
public class ContractTestMapTest {

	private ContractTestMap map;

	/**
	 * constructor
	 */
	public ContractTestMapTest() {
		map = new ContractTestMap();
	}

	/**
	 * Test that classes that extend interfaces pick up the interfaces.
	 */
	@Test
	public void getAllInterfacesTest() {
		Set<Class<?>> interfaces = map.getAllInterfaces(CImpl.class);
		Assert.assertTrue(interfaces.contains(A.class));
		Assert.assertTrue(interfaces.contains(B.class));
		Assert.assertTrue(interfaces.contains(C.class));
		Assert.assertEquals(3, interfaces.size());
	}

	/**
	 * Test that classes that extend interfaces pick up the interfaces.
	 */
	@Test
	public void getAllInterfacesTestWithInterface() {
		Set<Class<?>> interfaces = map.getAllInterfaces(C.class);
		Assert.assertTrue(interfaces.contains(A.class));
		Assert.assertTrue(interfaces.contains(B.class));
		Assert.assertTrue(interfaces.contains(C.class));
		Assert.assertEquals(3, interfaces.size());
	}

	/**
	 * Test that classes that extend interfaces pick up the interfaces.
	 */
	@Test
	public void getAllInterfacesTestWithBaseInterface() {
		Set<Class<?>> interfaces = map.getAllInterfaces(A.class);
		Assert.assertTrue(interfaces.contains(A.class));
		Assert.assertEquals(1, interfaces.size());
	}

	/**
	 * Test that classes that extend classes that have interfaces pick up the
	 * interfaces.
	 */
	@Test
	public void getAllInterfacesClassExtensionTest() {
		Set<Class<?>> interfaces = map.getAllInterfaces(EImpl.class);
		Assert.assertTrue(interfaces.contains(A.class));
		Assert.assertTrue(interfaces.contains(B.class));
		Assert.assertTrue(interfaces.contains(C.class));
		Assert.assertEquals(3, interfaces.size());
	}

	/**
	 * Get getInfo by test class fails with interface
	 */
	@Test
	public void getInfoByTestClassTestWithInterface() {
		TestInfo ti = map.getInfoByTestClass(A.class);
		Assert.assertNull("Should be null", ti);
	}

	/**
	 * Verify get Info is correct for test class
	 */
	@Test
	public void getInfoByTestClassTest() {
		TestInfo ti = map.getInfoByTestClass(AT.class);
		Assert.assertEquals(AT.class, ti.getContractTestClass());
		Assert.assertEquals(A.class, ti.getClassUnderTest());
	}

	/**
	 * Show that getInfoBytestclass returns null for implementation.
	 */
	@Test
	public void getInfoByTestClassTestWithImplementation() {
		TestInfo ti = map.getInfoByTestClass(AImpl.class);
		Assert.assertNull("Should be null", ti);
	}

	/**
	 * Show that retrieval of test classes works.
	 */
	@Test
	public void getAnnotatedClassesTest() {
		TestInfo ti = map.getInfoByTestClass(AT.class);
		Set<TestInfo> answer = new HashSet<TestInfo>();
		map.getAnnotatedClasses(answer, ti);
		List<String> lst = new ArrayList<String>();
		for (TestInfo t : answer) {
			lst.add(t.toString());
		}

		Assert.assertTrue("Missing AT", lst.contains(ti.toString()));
		Assert.assertTrue("Missing BadAbstract", lst.contains("[BadAbstract testing A]"));
		Assert.assertTrue("Missing BadNoInject", lst.contains("[BadNoInject testing A]"));
		Assert.assertEquals(3, lst.size());

	}

	/**
	 * Show that retrieval of test classes works.
	 */
	@Test
	public void getAnnotatedClassesTestWithFilter() {
		map = new ContractTestMap(new NameClassFilter(BadAbstract.class.getName()));
		TestInfo ti = map.getInfoByTestClass(AT.class);
		Set<TestInfo> answer = new HashSet<TestInfo>();
		map.getAnnotatedClasses(answer, ti);
		List<String> lst = new ArrayList<String>();
		for (TestInfo t : answer) {
			lst.add(t.toString());
		}

		Assert.assertTrue("Missing AT", lst.contains(ti.toString()));
		Assert.assertTrue("Missing BadNoInject", lst.contains("[BadNoInject testing A]"));
		Assert.assertEquals(2, lst.size());

	}

}
