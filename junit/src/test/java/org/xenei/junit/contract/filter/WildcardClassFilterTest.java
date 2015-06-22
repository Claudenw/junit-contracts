/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xenei.junit.contract.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.xenei.junit.contract.filter.parser.Parser;

/**
 * Test WildcardClassFilter
 *
 */
public class WildcardClassFilterTest {

	private final ClassFilter filter_sens;
	private final ClassFilter filter_insens;

	private Class<?> t = ClassFilter.class;
	private Class<?> f = String.class;

	/**
	 * Constructor.
	 */
	public WildcardClassFilterTest() {
		filter_sens = new WildcardClassFilter(Case.SENSITIVE,
				"*xene?.*ClassFilter");
		filter_insens = new WildcardClassFilter(Case.INSENSITIVE,
				"*Xene?.*ClassFilter");
	}

	/**
	 * Test that accept(Class) works
	 */
	@Test
	public void testAcceptClass() {
		assertTrue(filter_sens.accept(t));
		assertTrue(filter_insens.accept(t));

		assertFalse(filter_sens.accept(f));
		assertFalse(filter_insens.accept(f));
	}

	/**
	 * Test that accept(String) works.
	 */
	@Test
	public void testAccceptString() {

		assertTrue(filter_sens.accept(t.getName()));
		assertTrue(filter_insens.accept(t.getName()));

		assertFalse(filter_sens.accept(t.getName().toUpperCase()));
		assertTrue(filter_insens.accept(t.getName().toUpperCase()));

		assertFalse(filter_sens.accept(f.getName()));
		assertFalse(filter_insens.accept(f.getName()));
	}

	/**
	 * Test that toString() works.
	 */
	@Test
	public void testToString() {
		assertEquals("Wildcard( Sensitive, *xene?.*ClassFilter )",
				filter_sens.toString());
		assertEquals("Wildcard( Insensitive, *Xene?.*ClassFilter )",
				filter_insens.toString());
	}

	/**
	 * Test that the dot does not get expanded to regex expression.
	 */
	@Test
	public void testDotPosition() {
		assertEquals("^\\Q.org.xenei.\\E$",
				WildcardClassFilter.makeRegex(".org.xenei."));
	}

	/**
	 * Test that the asterisk is expanded to .* whereever it is found in the
	 * string.
	 */
	@Test
	public void testAsteriskPosition() {
		assertEquals("^.*\\Qorg\\E.*\\Qxenei\\E.*$",
				WildcardClassFilter.makeRegex("*org*xenei*"));
		assertEquals("^.*\\Q.bad.\\E.*$",
				WildcardClassFilter.makeRegex("*.bad.*"));
	}

	/**
	 * Test that the asterisk is expanded to .* whereever it is found in the
	 * string.
	 */
	@Test
	public void testSingleWildcards() {
		assertEquals("^.*$",
				WildcardClassFilter.makeRegex("*"));
		assertEquals("^.$",
				WildcardClassFilter.makeRegex("?"));
	}
	
	/**
	 * Test that the question mark is expanded to . (dot) where ever it is found
	 * in the string.
	 */
	@Test
	public void testQuestionPosition() {
		assertEquals("^.\\Qorg\\E.\\Qxenei\\E.$",
				WildcardClassFilter.makeRegex("?org?xenei?"));
	}

	/**
	 * Test that the parser parses string representation correctly.
	 * 
	 * @throws Exception
	 *             on any Exception.
	 */
	@Test
	public void testParse() throws Exception {
		Parser p = new Parser();

		ClassFilter cf = p.parse(filter_sens.toString());
		assertTrue("Wrong class", cf instanceof WildcardClassFilter);
		String[] args = cf.args();
		assertEquals(Case.SENSITIVE.toString(), args[0]);
		assertEquals("*xene?.*ClassFilter", args[1]);

		cf = p.parse(filter_insens.toString());
		assertTrue("Wrong class", cf instanceof WildcardClassFilter);
		args = cf.args();
		assertEquals(Case.INSENSITIVE.toString(), args[0]);
		assertEquals("*Xene?.*ClassFilter", args[1]);

	}
}
