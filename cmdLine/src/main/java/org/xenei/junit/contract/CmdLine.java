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

package org.xenei.junit.contract;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xenei.junit.contract.tooling.InterfaceReport;
import org.xenei.junit.contract.filter.ClassFilter;
import org.xenei.junit.contract.filter.parser.Parser;

/**
 * 
 * Class to produce report data about the state of the contract tests.
 *
 */
public class CmdLine {
	
	/**
	 * Run the interface report generation.
	 *
	 * use -h argument for help and argument list.
	 *
	 * @param args
	 *            the command line arguments.
	 * @throws ParseException
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(final String[] args) throws ParseException,
			MalformedURLException, IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		final CommandLine commands = new BasicParser()
				.parse(getOptions(), args);

		if (commands.hasOption("h")) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("CmdLine", getOptions());
			System.exit(0);
		}

		if (!commands.hasOption("p")) {
			System.out.println("At least on package must be specified");
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("CmdLine", getOptions());
			System.exit(1);
		}

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (commands.hasOption("d")) {
			final String[] dirs = commands.getOptionValues("d");
			URL[] urls = null;
			urls = new URL[dirs.length];
			for (int i = 0; i < dirs.length; i++) {
				urls[i] = new File(dirs[i]).toURI().toURL();
			}
			classLoader = new URLClassLoader(urls, classLoader);
		}

		ClassFilter filter = null;
		if (commands.hasOption("c"))
		{
			 filter = new Parser().parse(commands
						.getOptionValue("c"));
		}
		final InterfaceReport ifReport = new InterfaceReport(
				commands.getOptionValues("p"), filter , classLoader);

		if (commands.hasOption("u")) {
			System.out.println("Untested Interfaces");
			
			ClassFilter f = new Parser().parse(commands
					.getOptionValue("u"));
			for (final Class<?> c : f.filter(ifReport.getUntestedInterfaces())) {
				System.out.println(c.getCanonicalName());
			}
			System.out.println("End of Report");
		}

		if (commands.hasOption("i")) {
			System.out.println("Missing contract test implementations");
			ClassFilter f = new Parser().parse(commands
					.getOptionValue("i"));
			for (final Class<?> c : f.filter(ifReport.getUnImplementedTests())) {
				System.out.println(c.getName());
			}
			System.out.println("End of Report");
		}

		if (commands.hasOption("e")) {
			System.out.println("Misconfigured contract test report");
			for (final Throwable t : ifReport.getErrors()) {
				System.out.println(t.toString());
			}
			System.out.println("End of Report");
		}
	}

	// the loptions
	private static Options getOptions() {
		final Options retval = new Options();

		retval.addOption("h", "help", false, "Display this help page");
		retval.addOption("p", "package", true, "Package to be scanned");
		retval.addOption("d", "directory", true,
				"Directory to be scanned for classes");
		retval.addOption("u", "untested", true,
				"Filter for classes to include in the untested class report.  If not set no untested class report is generated.  Suggest: true()");
		retval.addOption("i", "implementation", false,
				"Filter for classes to include in the missing implementation class report.  If not set no missing implementation class report is generated.  Suggest: true()");
		retval.addOption("e", "errors", false,
				"Produce contract test configuration error report");
		retval.addOption(
				"c",
				"classFilter",
				true,
				"A class filter function. Classes that pass the filter will be included.  Default to true() ");
		
		return retval;
	}

}
