junit-contracts: A contract test suite runner 
=============================================

A suite runner for use with JUnit @RunWith annotation to run contract tests for interfaces.  Handles merging multiple tests from individual
contract tests into a single test suite for concrete implementations of one or more interfaces.

Introduces four annotations:

* @Contract - To map contract tests to the interfaces they test. 
* @Contract.Inject - To identify the producer of the object under test. 
* @ContractImpl - To identify the class under test in some ContractSuites. 
* @Dynamic.Inject - To identify the master producer for dynamic suites.

Introduces one class

* ContractSuite - used in the JUnit @RunWith to identify a contract testing suite.

Introduces two interfaces

* Producer - defines a producer that creates new instances of the object under test and can clean up after the test is run. 
* Dynamic - defines a dynamic test suite.  Dynamic test suites produce a list of tests after the suite is instantiated.

Maven Repository Info 
---------------------

     Group Id: org.xenei 
     Artifact Id: junit-contracts 
     Version: 0.0.2-SNAPSHOT


Contract Tests 
==============

Contract tests test the contract defined by a Java interface and the associated documentation.  The interface defines the method signatures,
but the documentation often expands upon that to define the behaviour that the interface is expected to perform. For example, the Map
interface defines put(), get(), and remove().  But the human readable documentation tells the developer that if you put() an object you must
be able to get() it unless the remove() has been called.  That last statement defines two tests in the contract test.

A more extreme case is java.io.Serializable where there are no methods to test but the documentation tells us that all serializable objects
must contain only serializable objects or implement three private methods with very specific signatures (2 methods if before Java 1.4).  The
user of the Serializable interface means that all classes derived from Serializable classes are themselves serializable.  See the
Serializable javadoc for details.  (An example contract test for the Serializable interface is provided in the examples for junit-contract.)

The basic argument for the use of contract tests is that you can prove code correctness.  That is, if every object interface is defined as
an interface, every interface has a contract test that covers all methods and their expected operation, and all objects have tests that mock
the objects they call as per the interface definition - then running the entire suite of tests demonstrates that the interconnection between
each object works.

If we know that A calls B properly and B calls C properly then we can infer that A calls C properly.  We can, with some work, prove that the
code is correct.

Contract tests will not discover misconfiguration.  For example, if class A uses a map and expects a map that can accept null keys but the
configuration specifies a map implementation that does not accept null keys, the contract test will not detect the error.  But then the
error is a configuration error caused by a missing requirement for the configuration.


For more information on contract tests see

http://www.infoq.com/presentations/integration-tests-scam

http://www.jbrains.ca/permalink/interlude-basic-correctness

How it Works 
============

         A
         |
      ---+---.  
      |      |
      A1     A2

Most JUnit tests work vertically.  Originally they were all derived from a test class, and since version 4 they use annotations to identify
a method as a test.  However, they only work in the direct hierarchy of class inheritance. That is, if classes A1 and A2 are derived from
class A and tests A1Test and A2Test are derived from ATest then all the ATest tests will be executed when A1Test and A2Test are run. 
However, with the use of interfaces there arises an orthagonal testing problem.

         A
         |
      ---+---.  
      |      |
      A1(I)  A2  B(I)

If we add interface I to our class structure and define A1 as implementing I and a new class B as implementing I, then both A1 and B require
the I tests, but they have no classes in common in the hierarchy.  So the tests for A1 and B must implement them - this violates the
Don't-Repeat-Yourself (DRY) principle and will lead to problems if I changes and A1 B tests are not modified as well.

The ContractSuite solves this problem by requiring the following additions:

* an abstract test class ITest for the interface I, that is annotated with the @Contract( I.class ). 
* a contract test suite that is annotated with @RunWith( ContractSuite.class ) and implements method creating a Producer and is 
annotated with @Contract.Inject.

Then when executing the suite: 
* discovers all of the @Contract mappings between tests and interfaces. 
* finds all the implemented interfaces of the object under test and adds their contract tests to the suite. 
* executes the completed suite.

The result is that every class that has a contract suite defined will verify that its implementation of the interface is correct as per the
contract test.  If the contract test changes, the class test will use the new contract test implementation.

Samples and Examples
====================

The test code contains a number of examples and is well [documented](tree/master/src/test/java/org/xenei/junit/contract/README.md) for use as such.

The sample code tree includes a sample for the Serializable contract test.
