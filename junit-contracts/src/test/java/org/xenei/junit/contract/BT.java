package org.xenei.junit.contract;

import org.junit.Test;

@Contract( B.class )
public abstract class BT {

  private B b;
  
  @Contract.Inject
  public void setB(B b)
  {
    this.b=b;
  }
	
	@Test
	public void testGetBName()
	{
		System.out.println( "testGetBName: "+b.getBName() );
	}

}
