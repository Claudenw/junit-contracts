maven-contracts: A Maven plugin to generate contract reports 
============================================================

A maven plugin that analyzes the contract testing environment, it produces
three reports:

* A list of interfaces that do not have contract tests defined for it.
* A list of classes that do not have contract suites defined but that implement interfaces that have contract tests defined.
* A list of errors that occurred when the tests were configured.

The plugin has four parameters:

packages
--------
    
A list of packages to process. Includes sub packages.  General format is 

     &lt;packages>
         &lt;package>some.package.name&lt;/package>
     &lt;/packages>

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

Several of these parameters use the ReportConfig object.  It has two settings:

* _reporting_, which defaults to true, indicates that the report should be generated.
* _failOnError_, which defaults to false, indicates that the build should fail if there is an error reported in the reports.
 
An example of usage:

     <unimplemented>
          <report>true</report>
          <failOnError>false</failOnError>
     </unimplemented>


