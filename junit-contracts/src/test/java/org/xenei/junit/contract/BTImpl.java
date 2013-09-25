package org.xenei.junit.contract;

import org.junit.Before;

public class BTImpl extends BT {

  @Before
  public final void setupBTImpl() {
    this.setB( new B(){
			public String getBName() {
				return "bname";
			}});
	}
	
}
