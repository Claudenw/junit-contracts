package org.xenei.junit.contract;

import org.junit.Test;

@Contract( C.class )
public abstract class CT {

	private C c;
	
  @Contract.Inject
	public void setC( C c )
	{
	  this.c = c;
	}
	
	@Test
	public void testGetBName()
	{
		System.out.println( "testGetCName: "+c.getCName());
	}

}
