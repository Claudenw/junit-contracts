package org.xenei.junit.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The container for repeated ContractExclude annotations.
 * 
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContractExcludes {
    /**
     * 
     * The list of ContractExclude annotations.
     */
    ContractExclude[] value();
}
