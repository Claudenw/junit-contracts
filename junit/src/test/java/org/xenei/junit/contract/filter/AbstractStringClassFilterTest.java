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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Class to test AbstractStringClassFilter.
 *
 */
public class AbstractStringClassFilterTest {
	private AbstractStringClassFilter filter = new AbstractStringClassFilter(
			"foo") {

		@Override
		public boolean accept(String className) {
			return true;
		}
	};
	private AbstractStringClassFilter filter_sens = new AbstractStringClassFilter(
			Case.SENSITIVE, "foo") {

		@Override
		public boolean accept(String className) {
			return true;
		}
	};
	private AbstractStringClassFilter filter_insens = new AbstractStringClassFilter(
			Case.INSENSITIVE, "foo") {

		@Override
		public boolean accept(String className) {
			return true;
		}
	};

	/**
	 * Test that toString() works.
	 */
	@Test
	public void testToString() {
		assertEquals("( Sensitive, foo )", filter.toString());
		assertEquals("( Sensitive, foo )", filter_sens.toString());
		assertEquals("( Insensitive, foo )", filter_insens.toString());

		filter.addString(ClassFilter.class.getName());
		filter_sens.addString(ClassFilter.class.getName());
		filter_insens.addString(ClassFilter.class.getName());
		assertEquals("( Sensitive, foo, " + ClassFilter.class.getName() + " )",
				filter.toString());
		assertEquals("( Sensitive, foo, " + ClassFilter.class.getName() + " )",
				filter_sens.toString());
		assertEquals("( Insensitive, foo, " + ClassFilter.class.getName()
				+ " )", filter_insens.toString());

	}

	/**
	 * Test that addStrings( String... ) works.
	 */
	@Test
	public void testAddStrings() {
		filter.addStrings("fu", "bar", "baz");
		assertEquals(4, filter.getStrings().size());
		assertTrue("missing fu", filter.getStrings().contains("fu"));
		assertTrue("missing bar", filter.getStrings().contains("bar"));
		assertTrue("missing baz", filter.getStrings().contains("baz"));
	}

	/**
	 * Test that addStrings( List&lt;String&gt; ) works.
	 */
	@Test
	public void testAddStrings_list() {
		List<String> lst = new ArrayList<String>();
		lst.add("fu");
		lst.add("bar");
		lst.add("baz");
		filter_sens.addStrings(lst);
		assertEquals(4, filter.getStrings().size());
		assertTrue("missing fu", filter_sens.getStrings().contains("fu"));
		assertTrue("missing bar", filter_sens.getStrings().contains("bar"));
		assertTrue("missing baz", filter_sens.getStrings().contains("baz"));
	}

}
