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

public class PrefixClassFilterTest {
	
	private final ClassFilter filter_sens;
	private final ClassFilter filter_insens;
	
	private Class<?> t = ClassFilter.class;
	private Class<?> f = String.class;
	
	public PrefixClassFilterTest() {
		filter_sens = new PrefixClassFilter(Case.SENSITIVE, "org.xenei");
		filter_insens = new PrefixClassFilter(Case.INSENSITIVE, "org.Xenei");
	}
	
	@Test
	public void testAcceptClass()
	{
		assertTrue( filter_sens.accept( t ) );
		assertTrue( filter_insens.accept( t ));
		
		assertFalse( filter_sens.accept( f ) );
		assertFalse( filter_insens.accept( f ));
	}
	
	@Test
	public void testAccceptString()
	{
		
		assertTrue( filter_sens.accept( t.getName() ) );
		assertTrue( filter_insens.accept( t.getName() ));

		assertFalse( filter_sens.accept( t.getName().toUpperCase() ) );
		assertTrue( filter_insens.accept( t.getName().toUpperCase() ));
		
		assertFalse( filter_sens.accept( f.getName() ) );
		assertFalse( filter_insens.accept( f.getName() ));
	}
	
	@Test
	public void testToString()
	{
		assertEquals( "PrefixClassFilter[S](org.xenei)",filter_sens.toString() );
		assertEquals( "PrefixClassFilter[I](org.Xenei)",filter_insens.toString() );
	}
}
