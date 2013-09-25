package org.xenei.junit.contract;

import org.junit.runner.RunWith;

@RunWith( ContractSuite.class )
public class CTImpl extends CT {

  @Contract.Inject
  public final C getCImpl() {
    return new C(){
			public String getCName() {
				return "cname";
			}

      public String getAName() {
        return "cname version of aname";
      }

      public String getBName() {
        return "cname version of bname";
      }};
	}
	
}
