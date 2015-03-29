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
