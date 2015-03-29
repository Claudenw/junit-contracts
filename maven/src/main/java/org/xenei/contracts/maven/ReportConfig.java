package org.xenei.contracts.maven;

public class ReportConfig {
	private boolean reporting = true;
	private boolean failOnError = false;
	
	public ReportConfig() {}
	
	public void setReport(boolean reporting) {
		this.reporting = reporting;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public boolean isReporting() {
		return reporting;
	}
	public boolean isFailOnError() {
		return failOnError;
	}
	
}