package org.xenei.junit.contract;

import org.junit.Test;

@Contract( A.class )
public abstract class AT {

  private A a;
  
  @Contract.Inject  
	public void setA( A a )
  {
    this.a = a;
  }
	
	@Test
	public void testGetAName()
	{
		System.out.println( "testGetAName: "+a.getAName() );
	}

}
