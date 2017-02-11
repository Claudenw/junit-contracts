/*
 * This code provided as an example and is not to be considered complete or production ready.
 */
package contracttests;
import java.util.HashSet;
import java.util.TreeSet;
import org.junit.runner.RunWith;
import org.xenei.junit.contract.Contract;
import org.xenei.junit.contract.ContractImpl;
import org.xenei.junit.contract.ContractSuite;
import org.xenei.junit.contract.IProducer;

@RunWith(ContractSuite.class)
@ContractImpl(TreeSet.class)
public class TreeSetTest {
 
    IProducer<TreeSet<Object>> producer = new IProducer<TreeSet<Object>>() {
    	public TreeSet<Object> newInstance() {
    		return new TreeSet<Object>();
    	}
    	
    	public void cleanUp() {
    		// no cleanup required.
    	}
    };
    
    @Contract.Inject
    public IProducer<TreeSet<Object>> makedashSet() {
        return producer;        
    }
}
