package org.xenei.contracts.maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.xenei.junit.contract.tooling.InterfaceReport;

@Mojo(name = "contract-test", defaultPhase=LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
public class ContractMojo extends AbstractMojo {

	@Parameter(property = "packages")
	private String[] packages;

	@Parameter(property = "untested", defaultValue = "true")
	private boolean reportUntested;

	@Parameter(property = "unimplemented", defaultValue = "true")
	private boolean reportUnimplemented;

	@Parameter(property = "showErrors", defaultValue = "true")
	private boolean reportErrors;

	@Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
	private File classDir;

	@Parameter(defaultValue = "${project.build.testOutputDirectory}", readonly = true)
	private File testDir;

	@Parameter(defaultValue = "${project.build.directory}", readonly = true)
	private File target;

	private File myDir;

	public ContractMojo() {
	}

	public void setPackages(final String[] packages) {
		this.packages = packages;
	}

	@Override
	public void execute() throws MojoExecutionException {

		System.out.print( "PACKAGES: ");
		for (String s : packages)
		{
			System.out.println( "PKG: "+s);
		}
		
		if ((packages == null) || (packages.length == 0)) {
			throw new MojoExecutionException(
					"At least one package must be specified");
		}

		myDir = new File(target, "contract-reports");
		if (!myDir.exists()) {
			myDir.mkdirs();
		}

		InterfaceReport ir;
		try {
			ir = new InterfaceReport(packages, null, Thread.currentThread()
					.getContextClassLoader());
		} catch (MalformedURLException e1) {
			throw new MojoExecutionException(
					"Could not create Interface report class", e1);
		}

		if (reportUntested) {
			try {
				doReportUntested(ir);
			} catch (final IOException e) {
				throw new MojoExecutionException(
						"Unable to write untested report", e);
			}
		}

		if (reportUnimplemented) {
			try {
				doReportUnimplemented(ir);
			} catch (final IOException e) {
				throw new MojoExecutionException(
						"Unable to write unimplemented report", e);
			}
		}

		if (reportErrors) {
			try {
				doReportErrors(ir);
			} catch (final IOException e) {
				throw new MojoExecutionException(
						"Unable to write error report", e);
			}
		}
	}

	private void doReportUntested(final InterfaceReport ir) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(myDir,
					"untested.txt")));
			for (final Class<?> c : ir.getUntestedInterfaces()) {
				bw.write(c.getCanonicalName());
				bw.newLine();
			}
		} finally {
			IOUtils.closeQuietly(bw);
		}
	}

	private void doReportUnimplemented(final InterfaceReport ir)
			throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(myDir,
					"unimplemented.txt")));
			for (final Class<?> c : ir.getUnImplementedTests()) {
				bw.write(c.getCanonicalName());
				bw.newLine();
			}
		} finally {
			IOUtils.closeQuietly(bw);
		}
	}

	private void doReportErrors(final InterfaceReport ir) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(
					new File(myDir, "errors.txt")));
			for (final Throwable t : ir.getErrors()) {
				bw.write(t.toString());
				bw.newLine();
			}
		} finally {
			IOUtils.closeQuietly(bw);
		}
	}
}
