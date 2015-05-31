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

import org.junit.Test;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.exampleTests.A;
import org.xenei.junit.contract.exampleTests.AT;
import org.xenei.junit.contract.filter.parser.Parser;

public class HasAnnotationClassFilterTest {
	private ClassFilter filter = new HasAnnotationClassFilter(Contract.class);
	private Class<?> t = AT.class;
	private Class<?> f = A.class;

	@Test
	public void testAcceptClass() {
		assertTrue(filter.accept(t));
		assertFalse(filter.accept(f));
	}

	@Test
	public void testAccceptString() {

		assertTrue(filter.accept(t.getName()));
		assertFalse(filter.accept(f.getName()));
	}

	@Test
	public void testToString() {
		assertEquals("HasAnnotation( " + Contract.class.getName() + " )",
				filter.toString());
	}

	@Test
	public void testParse() throws Exception {
		Parser p = new Parser();

		ClassFilter cf = p.parse(filter.toString());
		assertTrue("Wrong type", cf instanceof HasAnnotationClassFilter);
		String[] args = cf.args();
		assertEquals(1, args.length);
		assertEquals(Contract.class.getName(), args[0]);
	}
}
