contract-test-maven-plugin: A Maven plugin to generate contract reports 
=======================================================================

A maven plugin that analyzes the contract testing environment, it produces
three reports:

* A list of interfaces that do not have contract tests defined for it.
* A list of classes that do not have contract suites defined but that implement interfaces that have contract tests defined.
* A list of errors that occurred when the tests were configured.

The plugin has five parameters:

packages
--------
    
A list of packages to process. Includes sub packages.  General format is 

     <packages>
         <package>some.package.name</package>
     </packages>
     
skipFilter
----------

A string that defines a Class filter of classes to skip.  If not set no classes are skipped.


error
-----

Defines a report configuration (ReportConfig) for errors generated during run.

unimplemented
-------------

Defines a report configuration (ReportConfig) for unimplemented tests. Unimplemented tests are classes that implement an interface that has a Contract test but for which no contract suite test implementation is found.

untested
--------

Defines a report configuration (ReportConfig) for untested interfaces. Untested interfaces are interfaces that are defined in the list of packages but that do not have contract tests and are not annotated with NoContractTest.
Parameter Details


ReportConfig
============

Several of these parameters use the ReportConfig object.  It has three settings:

* _reporting_, which defaults to true, indicates that the report should be generated.
* _failOnError_, which defaults to false, indicates that the build should fail if there is an error reported in the reports.
* _filter_, a string that defines a class filter of classes to include in the report.  Not setting this results in all classes being processed for the report.  See <a href="../classfilters">Class Filters</a> for details of filter specification.
 
An example of usage:

     <unimplemented>
          <report>true</report>
          <failOnError>false</failOnError>
          <filter>Not( Wildcard( *.test.* ))</filter>
     </unimplemented>

