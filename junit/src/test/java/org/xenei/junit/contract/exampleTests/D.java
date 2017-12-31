package org.xenei.junit.contract.exampleTests;

/**
 * A complex interface that returns instances of other interfaces.
 * 
 * This interface is used for the dynamic suite testing.
 * 
 */
interface D {
    public String getDName();

    public A getA();

    public B getB();

}
