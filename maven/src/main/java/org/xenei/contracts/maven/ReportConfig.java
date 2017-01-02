package org.xenei.contracts.maven;

import org.codehaus.plexus.util.StringUtils;
import org.xenei.classpathutils.ClassPathFilter;
import org.xenei.classpathutils.filter.parser.Parser;

/**
 * Configuration for a report
 *
 */
public class ReportConfig {
	private boolean reporting = true;
	private boolean failOnError = false;
	private ClassPathFilter filter = ClassPathFilter.TRUE;

	/**
	 * Constructor
	 */
	public ReportConfig() {
	}

	/**
	 * If true this report will be generated. Defaults to <code>true</code>
	 * 
	 * @param reporting
	 *            true if this report should be generated.
	 */
	public void setReport(boolean reporting) {
		this.reporting = reporting;
	}

	/**
	 * If true the build will fail on an error in this report. Defaults to
	 * <code>false</code>
	 * 
	 * @param failOnError
	 *            if true the build will fail on error.
	 */
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	/**
	 * Returns true if this report is enabled.
	 * 
	 * @return returns true if this report is enabled.
	 */
	public boolean isReporting() {
		return reporting;
	}

	/**
	 * Returns true if this report generates a build error on errors.
	 * 
	 * @return true if this report generates a build error on errors.
	 */
	public boolean isFailOnError() {
		return failOnError;
	}

	/**
	 * Gets the filter
	 * 
	 * @return the filter.
	 */
	public ClassPathFilter getFilter() {
		return filter;
	}

	/**
	 * Set the class filter. Only classes that pass the filter will be included.
	 * By default the filter accepts all classes. Passing a null or null length
	 * string will result in all classes passing the test.
	 * 
	 * @param filter
	 *            The string representation of the filter. May be null.
	 * @throws IllegalArgumentException if the filter can not be parsed.
	 */
	public void setFilter(String filter) throws IllegalArgumentException {
		if (StringUtils.isBlank(filter)) {
			this.filter = ClassPathFilter.TRUE;
		} else {
			this.filter = new Parser().parse(filter);
		}
	}

}