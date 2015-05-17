junit-contracts tests: A series of examples
===========================================

The test cases use a series of interfaces, parallel implementations and tests.  The interface structure is

         A     B
         |     |
         `--+--'
            |
            C   D
            
A -- Interface
* AT -- Is annotated with @Contract(A.class) and contains the test methods for A
* AImpl -- Implements A
* AImplTest -- Is annotated with @RunWith(ContractTestRunner.class) to run only AT tests.
* AImplContractTest -- Is annotated with @RunWith(ContractSuite.class) and @ContractImpl(AImpl.class), runs AT tests.
          
B -- Interface
* BT -- Is annotated with @Contract(B.class) and contains the test methods for B
* BImpl -- Implements B
* BImplTest -- Is annotated with @RunWith(ContractTestRunner.class) to run only BT tests.
* BImplContractTest -- Is annotated with @RunWith(ContractSuite.class) and @ContractImpl(BImpl.class), runs BT tests.
    
C -- Interface extends A and B
* CT -- Is annotated with @Contract(C.class) and contains the test methods for C
* CImpl -- Implements C
* CImplTest -- Is annotated with @RunWith(ContractTestRunner.class) to run only CT tests.
* CImplContractTest -- Is annotated with @RunWith(ContractSuite.class) and @ContractImpl(CImpl.class), runs AT, BT and CT tests.
   
D -- Interface that returns instances of A and B
* DT -- Is annotated with @Contract(D.class) and contains the test methods for D
* DImpl -- Implements D
* DImplTest -- Is annotated with @RunWith(ContractTestRunner.class) to run only DT tests.
* DImplContractTest -- Is annotated with @RunWith(ContractSuite.class) and @ContractImpl(DImpl.class)
            

                         AT>>>A       B<<<BT               
                          |  /|      /|   |             
               AImplTest--' / |     / |   BImplTest           
                           /  |    /  |                
      BImplContractTest   /   |   /   |            
                       \ /    `--/+---'
                        /       / |                       D<<<DT
     AImplContractTest / \     /  |                      /     |              
                 \    /   \   /   |  DImplContractTest  /      DImplTest       
                  AImpl   BImpl   |                   \/                       
                   |       |      C<<CT             DImpl                         
                   |       |      /   |                                        
                   |       |     /    CImplTest                                
                   |       |    /                 ##########################################
                   `---+---'   /                  #       LEGEND                           #  
                       |      /                   # /       = bottom implements top        # 
    CImplContractTest  |     /                    # | and - = extends                      #
                     \ |    /                     # > and < = contract test for            #
                      \|   /                      # \       = top contract impl for bottom #
                       CImpl                      ########################################## 
   
   

Testing Methods not in the Interface
------------------------------------

CImpl2 is a second implementation of the C interface but this class also adds an additional method to test.  
The extra method, called extraMethod(), is tested in the CImpl2ContractTest by adding the test methods directly 
to the contract test class.

Testing Return Values of Classes
--------------------------------

The D interface defines two methods that return other interfaces: getA() and getB().  The DImplContractTest only tests that an A or B is returned.  The DTImplSuite class runs the contract tests on the returned values.  It does this by using the Dynamic suite pattern.

Dynamic Suites
==============

Dynamic suites are test suites where the member classes are not known until after the suite instance is created.
To create a Dynamic suite the test suite annotated with @RunWith( Contract.Suite ) must extend the Dynamic 
interface.  

Logically, Dynamic suites create an Instance of IProducer that produces objects that can be used directly in 
the enclosed tests or can create the objects that can be used in the enclosed tests.  Using our D interface 
example, our Dynamic suite will create IProducer<DImpl> producers.  However, to run the AT contract test on the 
results returned from getA() will require that the IProducer<Dimpl> be used to create an instance of 
IProducer<A>.  

To make this happen:
* The Dynamic.Inject annotation is used in the suite to identify the method that will create the producer for 
the suite. (e.g. IProducer&lt;DImpl>)
* Each class in the suite that is annotated with @RunWith( ContractSuite.class ) will require a Contract.Inject 
annotated setter that sets accepts the producer created by the Dynamic.Inject annotated method. 
(e.g IProducer&lt;DImpl>)
* Each class in the suite that is annotated with @RunWith( ContractSuite.class ) will require a Contract.Inject 
annotated getter that sets returns the producer required by the contract test to be run. (e.g. IProducer&lt;A> )

This results in a pattern like:

    @RunWith(ContractSuite.class)
    @ContractImpl(DImpl.class)
    public class DTImplSuite implements Dynamic {
        private IProducer<DImpl> producer = ...
        
        @Dynamic.Inject
        public IProducer<DImpl> getInjected() { ... }
        
        @Override
        public List<Class<?>> getSuiteClasses() { ... }
        
        // see DTImplSuite.java for explination of this interface
        interface ForceA extends A {}; 
        @ContractImpl(ForceA.class)
        @RunWith(ContractSuite.class)
        protected static class ATest {
            // the master producer
            private IProducer<DImpl> producerD;
            
            // the producer we need for the A interface test
            private IProducer<A> producer;
            
            public ATest() {};
            
            @Contract.Inject
            public void setProducer(IProducer<DImpl> producerD) { ... }
            
            @Contract.Inject
            public IProducer<A> getProducer() { ... }
        }
    }
    
    
The DTImplSuite test case implements this pattern.



