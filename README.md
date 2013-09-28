junit-contracts
===============

Contract test suite runner.

A suite runner for use with Junit @RunWith annotation to run contract tests for interfaces.  Handles merging multiple
tests from individual abstract classes into a single test suite.

Initial implementation is functional but not yet fully baked.

Future direction includes the use of producer classes rather than test class getters and setters.

current implementation create a single instance of the class under test at the start of the test suite not at the start
of each test ala @Before annotation.  This should be fixed shortly.

For more information on contract tests see


http://www.infoq.com/presentations/integration-tests-scam

http://www.jbrains.ca/permalink/interlude-basic-correctness
