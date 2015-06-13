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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xenei.junit.contract.tooling.InterfaceReport;
import org.xenei.junit.contract.filter.parser.Parser;

/**
 * 
 * Class to produce report data about the state of the contract tests.
 *
 */
public class CmdLine {

	private static final Logger LOG = LoggerFactory
			.getLogger(CmdLine.class);

	
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
			formatter.printHelp("InterfaceReport", getOptions());
			System.exit(0);
		}

		if (!commands.hasOption("p")) {
			System.out.println("At least on package must be specified");
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("InterfaceReport", getOptions());
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

		final InterfaceReport ifReport = new InterfaceReport(
				commands.getOptionValues("p"), new Parser().parse(commands
						.getOptionValue("s")), classLoader);

		if (commands.hasOption("u")) {
			System.out.println("Untested Interfaces");
			for (final Class<?> c : ifReport.getUntestedInterfaces()) {
				System.out.println(c.getCanonicalName());
			}
			System.out.println("End of Report");
		}

		if (commands.hasOption("i")) {
			System.out.println("Missing contract test implementations");
			for (final Class<?> c : ifReport.getUnImplementedTests()) {
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
		retval.addOption("u", "untested", false,
				"Produce untested class report");
		retval.addOption("i", "implementation", false,
				"Produce missing implementation report");
		retval.addOption("e", "errors", false,
				"Produce contract test configuration error report");
		retval.addOption(
				"s",
				"skipInterfaces",
				true,
				"A list of interfaces that should not have tests.  See also @NoContractTest annotation");
		retval.addOption("c", "skipClasses", true,
				"A list of classes that should not have tests.");

		return retval;
	}

}
