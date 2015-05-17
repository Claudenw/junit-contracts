package org.xenei.junit.contract.info;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;

public class TestInfoErrorRunner extends Runner {

	private final TestInfo testInfo;;

	private final Class<?> fTestClass;

	public TestInfoErrorRunner(final Class<?> testClass, final TestInfo testInfo) {
		fTestClass = testClass;
		this.testInfo = testInfo;
	}

	public void logErrors(final Logger log) {
		for (final Throwable t : testInfo.getErrors()) {
			log.error(t.toString());
		}
	}

	@Override
	public Description getDescription() {
		final Description description = Description
				.createSuiteDescription(testInfo.getContractTestClass());
		for (final Throwable each : testInfo.getErrors()) {
			description.addChild(describeCause(each));
		}
		return description;
	}

	@Override
	public void run(final RunNotifier notifier) {
		for (final Throwable each : testInfo.getErrors()) {
			runCause(each, notifier);
		}
	}

	private Description describeCause(final Throwable child) {
		return Description.createTestDescription(testInfo
				.getContractTestClass(), String.format("%s(%s)", testInfo
				.getContractTestClass().getSimpleName(), fTestClass
				.getSimpleName()));
	}

	private void runCause(final Throwable child, final RunNotifier notifier) {
		final Description description = describeCause(child);
		notifier.fireTestStarted(description);
		notifier.fireTestFailure(new Failure(description, child));
		notifier.fireTestFinished(description);
	}

	@Override
	public String toString()
	{
		return String.format( "TestInfoErrorRunner[ %s ]", testInfo);
	}
}
