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

package org.xenei.junit.contract.tooling;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.junit.Test;
import org.xenei.junit.contract.ClassPathUtils;

public class ClassPathUtilsTest {

	@Test
	public void testFindClassesFromClassJar() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("classes.jar");
		Set<String> names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit");
		assertEquals( 3, names.size() );
		names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit.contract.info");
		assertEquals( 1, names.size() );
		names = ClassPathUtils.findClasses(url.toString()+"!/", "com.xenei");
		assertEquals( 0, names.size() );
	}

	@Test
	public void testFindClassesFromJavadocJar() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("javadoc.jar");
		Set<String> names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit");
		assertEquals( 0, names.size() );
	}

	@Test
	public void testFindClassesFromSourceJar() throws IOException
	{
		URL url = ClassPathUtilsTest.class.getResource("sources.jar");
		Set<String> names = ClassPathUtils.findClasses(url.toString()+"!/", "org.xenei.junit");
		assertEquals( 0, names.size() );
	}

}
