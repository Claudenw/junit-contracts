package org.xenei.contracts.maven;

/**
 * Configuration for a report
 *
 */
public class ReportConfig {
	private boolean reporting = true;
	private boolean failOnError = false;
	
	public ReportConfig() {}
	
	/**
	 * If true this report will be generated.
	 * Defaults to <code>true</code>
	 * @param reporting true if this report should be generated.
	 */
	public void setReport(boolean reporting) {
		this.reporting = reporting;
	}

	/**
	 * If true the build will fail on an error in this report.
	 * Defaults to <code>false</code>
	 * @param failOnError if true the build will fail on error.
	 */
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	/**
	 * Returns true if this report is enabled.
	 * @return returns true if this report is enabled.
	 */
	public boolean isReporting() {
		return reporting;
	}
	
	/**
	 * Returns true if this report generates a build error on errors.
	 * @return true if this report generates a build error on errors.
	 */
	public boolean isFailOnError() {
		return failOnError;
	}
	
}