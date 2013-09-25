package org.xenei.junit.contract;

import org.junit.Before;

public class ATImpl extends AT {

  @Before
  public final void setupATImpl() {
    this.setA( new A(){
			public String getAName() {
				return "aname";
			}});
	}
	
}
