package org.xenei.contracts.maven;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class ContractMojoTest  extends AbstractMojoTestCase {
 
	public ContractMojoTest() {
		// TODO Auto-generated constructor stub
	}
	
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // required for mojo lookups to work
        super.setUp();
    }

    /**
     * @throws Exception
     */
    public void testMojoGoal() throws Exception
    {
        File testPom = new File( getBasedir(),
          "src/test/resources/unit/basic-test/basic-test-plugin-config.xml" );

        ContractMojo mojo = (ContractMojo) lookupMojo( "contract-test", testPom );

        assertNotNull( mojo );
    }


}
