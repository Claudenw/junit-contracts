package org.xenei.contracts.maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.xenei.junit.contract.ClassPathUtils;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.NoContractTest;
import org.xenei.junit.contract.tooling.InterfaceInfo;
import org.xenei.junit.contract.tooling.InterfaceReport;

@Mojo(name = "contract-test", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES, requiresDependencyResolution = ResolutionScope.TEST)
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

	@Component
	private MavenProject project;

	@Parameter(defaultValue = "${plugin.artifactMap}", required = true, readonly = true)
	private Map<String, Artifact> pluginArtifactMap;
	@Component
	private RepositorySystem repositorySystem;
	@Parameter(defaultValue = "${localRepository}", required = true, readonly = true)
	private ArtifactRepository localRepository;

	private Set<Artifact> junitContractsArtifacts;

	private File myDir;

	public ContractMojo() {
	}

	public void setPackages(final String[] packages) {
		this.packages = packages;
	}

	@Override
	public void execute() throws MojoExecutionException {

		if ((packages == null) || (packages.length == 0)) {
			getLog().error("At least one package must be specified");
			throw new MojoExecutionException(
					"At least one package must be specified");
		}

		if (getLog().isDebugEnabled()) {
			getLog().debug("PACKAGES: ");
			for (final String s : packages) {
				getLog().debug("PKG: " + s);
			}
		}

		myDir = new File(target, "contract-reports");
		if (!myDir.exists()) {
			myDir.mkdirs();
		}

		InterfaceReport ir;
		try {
			ir = new InterfaceReport(packages, null, buildClassLoader());
		} catch (final MalformedURLException e1) {
			throw new MojoExecutionException(
					"Could not create Interface report class", e1);
		}

		try {
			doReportInterfaces(ir);
		} catch (final IOException e) {
			throw new MojoExecutionException("Unable to write interface list",
					e);
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

	private void doReportInterfaces(final InterfaceReport ir)
			throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(myDir,
					"interfaces.txt")));
			for (InterfaceInfo ii : ir.getInterfaceInfoCollection()) {
				String entry = String.format("Interface: %s %s", ii.getName()
						.getName(), ii.getTests());
				if (getLog().isDebugEnabled()) {
					getLog().debug(entry);
				}
				bw.write(entry);
				bw.newLine();
			}

			for (Class<?> cls : ir.getPackageClasses()) {
				String entry = String.format(
						"Class: %s, contract: %s, impl: %s, flg: %s",
						cls.getName(),
						cls.getAnnotation(Contract.class) != null,
						cls.getAnnotation(ContractImpl.class) != null,
						cls.getAnnotation(NoContractTest.class) != null);
				if (getLog().isDebugEnabled()) {
					getLog().debug(entry);
				}
				bw.write(entry);
				bw.newLine();
			}

		} finally {
			IOUtils.closeQuietly(bw);
		}
	}

	private void doReportUntested(final InterfaceReport ir) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(myDir,
					"untested.txt")));
			for (final Class<?> c : ir.getUntestedInterfaces()) {
				bw.write(c.getName());
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
				bw.write(c.getName());
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

	private ClassLoader buildClassLoader() throws MojoExecutionException {
		final ClassWorld world = new ClassWorld();
		ClassRealm realm;

		try {
			realm = world.newRealm("contract", null);
			
			// gwt-dev and its transitive dependencies
			for (Artifact elt : getJunitContractsArtifacts()) {
				String dir= String.format( "%s!/", elt.getFile().toURI().toURL());
				if (getLog().isDebugEnabled()) {
					getLog().debug("Checking for imports from: " + dir);
				}
				try {
					Set<String> classNames = ClassPathUtils.findClasses(dir, "org.xenei.junit.contract");
					for (String clsName : classNames)
					{
						if (getLog().isDebugEnabled()) {
							getLog().debug("Importing from current classloader: " + clsName);
						}
						importFromCurrentClassLoader(realm, Class.forName(clsName));
					}
				} catch (ClassNotFoundException e) {
					throw new MojoExecutionException( e.toString(), e );
				} catch (IOException e) {
					throw new MojoExecutionException( e.toString(), e );
				}
			}
			
			for (final String elt : project.getCompileSourceRoots()) {
				final URL url = new File(elt).toURI().toURL();
				realm.addURL(url);
				if (getLog().isDebugEnabled()) {
					getLog().debug("Source root: " + url);
				}
			}

			// add Compile classpath
			for (final String elt : project.getCompileClasspathElements()) {
				final URL url = new File(elt).toURI().toURL();
				realm.addURL(url);
				if (getLog().isDebugEnabled()) {
					getLog().debug("Compile classpath: " + url);
				}
			}

			// add Test classpath
			for (final String elt : project.getTestClasspathElements()) {
				final URL url = new File(elt).toURI().toURL();
				realm.addURL(url);
				if (getLog().isDebugEnabled()) {
					getLog().debug("Test classpath: " + url);
				}
			}
			// // gwt-dev and its transitive dependencies
			// for (Artifact elt : getGwtDevArtifacts()) {
			// URL url = elt.getFile().toURI().toURL();
			// realm.addURL(url);
			// if (getLog().isDebugEnabled()) {
			// getLog().debug("Compile classpath: " + url);
			// }
			// }
			// realm.addURL(pluginArtifactMap.get("com.google.gwt:gwt-dev").getFile().toURI().toURL());
		} catch (final DuplicateRealmException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (final MalformedURLException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		} catch (final DependencyResolutionRequiredException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		// // Argument to Compiler ctor
		// importFromCurrentClassLoader(realm, CompilerOptions.class);
		// // Argument to Compiler#run
		// importFromCurrentClassLoader(realm, TreeLogger.class);
		// // Referenced by CompilerOptions; TreeLogger.Type is already imported
		// via TreeLogger above
		// importFromCurrentClassLoader(realm, JsOutputOption.class);
		// // Makes error check easier
		// importFromCurrentClassLoader(realm, UnableToCompleteException.class);
		return realm;
		// Thread.currentThread().setContextClassLoader(realm);
	}

	private void importFromCurrentClassLoader(final ClassRealm realm,
			final Class<?> cls) {
		if (cls == null) {
			return;
		}
		realm.importFrom(Thread.currentThread().getContextClassLoader(),
				cls.getName());
		// ClassRealm importing is prefix-based, so no need to specifically add
		// inner classes
		for (final Class<?> intf : cls.getInterfaces()) {
			importFromCurrentClassLoader(realm, intf);
		}
		importFromCurrentClassLoader(realm, cls.getSuperclass());
	}

	private Set<Artifact> getJunitContractsArtifacts() {
		if (junitContractsArtifacts == null) {
			ArtifactResolutionRequest request = new ArtifactResolutionRequest()
					.setArtifact(
							pluginArtifactMap.get("org.xenei:junit-contracts"))
					.setResolveTransitively(true)
					.setLocalRepository(localRepository);
			ArtifactResolutionResult result = repositorySystem.resolve(request);
			junitContractsArtifacts = result.getArtifacts();
		}
		return junitContractsArtifacts;
	}
}
