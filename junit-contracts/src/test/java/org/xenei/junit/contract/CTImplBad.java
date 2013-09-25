package org.xenei.junit.contract;

import org.junit.Before;

public class CTImplBad extends CT {

  @Before
  public final void setupCTImpl() {
    this.setC( new C(){
			public String getCName() {
				return "cname";
			}

      public String getAName() {
        return "cname version of aname";
      }

      public String getBName() {
        return "cname version of bname";
      }});
	}
	
}
