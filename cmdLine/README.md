contract-cmdline: Command line tools to run reports
===================================================

A command line tool that executes contract test reports.  Ensure that the classes you want to test are on the classpath along with the _contract-cmdLine_ and _junit-contracts_ jars.


      java org.xenei.junit.contract.CmdLine -h
       
      usage: CmdLine
       -c,--classFilter <arg>   A class filter function. Classes that pass the
                                filter will be included.  Default to true()
       -d,--directory <arg>     Directory to be scanned for classes
       -e,--errors              Produce contract test configuration error report
       -h,--help                Display this help page
       -i,--implementation      Filter for classes to include in the missing
                                implementation class report.  If not set no
                                missing implementation class report is
                                generated.  Suggest: true()
       -p,--package <arg>       Package to be scanned
       -u,--untested <arg>      Filter for classes to include in the untested
                                class report.  If not set no untested class
                                report is generated.  Suggest: true()

For information concerning how to define filters see <a href="../classfilters">Class Filters</a>.       
      

