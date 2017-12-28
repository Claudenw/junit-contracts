junit-contracts: A contract test suite runner 
=============================================

A suite runner for use with JUnit @RunWith annotation to run contract tests for interfaces.  Handles merging multiple tests from individual
contract tests into a single test suite for concrete implementations of one or more interfaces.

Introduces seven annotations:

* @Contract - To map contract tests to the interfaces they test. 
* @Contract.Inject - To identify the producer of the object under test. 
* @ContractTest - To identify methods in the text. Replaces junit @Test annotation.
* @ContractImpl - To identify the class under test in ContractSuites. 
* @Dynamic.Inject - To identify the master producer for dynamic suites.
* @NoContractTest - To identify interfaces that should not or do not yet have contract tests.  This only applies to interfaces that have methods as pure marker
interfaces are automatically ignored. 
* @ContractExclude - To exclude specific tests from being executed.

Introduces one class

* ContractSuite - used in the JUnit @RunWith to identify a contract testing suite.

Introduces two interfaces

* IProducer - defines a producer that creates new instances of the object under test and can clean up after the test is run. 
* Dynamic - defines a dynamic test suite.  Dynamic test suites produce a list of tests after the suite is instantiated.

Maven Repository Info 
---------------------

Release version

     Group Id: org.xenei 
     Artifact Id: junit-contracts 

Snapshot versions are hosted at:

     https://oss.sonatype.org/content/repositories/snapshots/

License
=======

The Xenei code in this project is all licensed under the Apache V2 license.  However, the jar 
includes classes from <a href="https://github.com/lukehutch/fast-classpath-scanner">fast-classpath-scanner</a> by <a href="https://github.com/lukehutch">Luke Hutchison</a> which is licensed under the MIT license.  The MIT and the Apache licenses are included in the jar.

Removing Bad Tests
==================

Known bad contract tests can be removed from consideration by adding 

     -Dcontracts.skipClasses=test1,test2
     
to the Java VM arguments.

For example our test code (as defined in the pom.xml) adds

    -Dcontracts.skipClasses=org.xenei.junit.bad.BadAbstrtact,org.xenei.junit.bad.BadNoInject

to skip our known bad tests. 

@ContractImpl tests
-------------------

The @ContractImpl has two attributes that can remove tests.

* skip is an array of interface classes that should not be tested.  All contract tests for the interfaces will be skipped.

* ignore list any @Contract annotated tests that should be ignored.  This allows removal of broken
tests that are outside the control of the developer of the @ContractImpl test.

@ContractExclude
----------------

The @ContractExclude annotation is intended to be used with @ContractImpl.  The annotation has 2 arguments
1. value is the name of the class that contains the test to exclude.
2. methods is a list of method names in the test class to exclude.

This annotation will remove the tests only for the ContractImpl it is associated with.

@Contract tests
---------------

The @Contract tests may be removed by adding the standard junit @Ignore annotation.  This will remove it from all contract testing.

  

