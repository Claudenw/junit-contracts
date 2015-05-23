/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xenei.junit.contract.filter;

import java.io.Serializable;
import java.util.List;

/**
 * A ClassFilter providing conditional AND logic across a list of
 * file filters. This filter returns {@code true} if all filters in the
 * list return {@code true}. Otherwise, it returns {@code false}.
 * Checking of the file filter list stops when the first filter returns
 * {@code false}.
 */
public class AndClassFilter 
        extends AbstractConditionalClassFilter
        implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7607072170374969854L;

	/**
     * Constructs a new instance of <code>AndClassFilter</code>.
     */
    public AndClassFilter() {
    	super();
    }

    /**
     * Constructs a new instance of <code>AndClassFilter</code>
     * with the specified list of filters.
     *
     * @param classFilters  a List of ClassFilter instances, copied, null ignored
     * @since 1.1
     */
    public AndClassFilter(final List<ClassFilter> classFilters) {
    	super(classFilters);
    }

    /**
     * Constructs a new instance of <code>AndClassFilter</code>
     * with the specified list of filters.
     *
     * @param classFilters  a List of ClassFilter instances, copied, null ignored
     * @since 1.1
     */
    public AndClassFilter(final ClassFilter... classFilters) {
    	super(classFilters);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(String className) {
    	List<ClassFilter> filters = this.getClassFilters();
    	
        if (filters.isEmpty()) {
            return false;
        }
        for (ClassFilter classFilter : filters) {
            if (!classFilter.accept(className)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Class<?> clazz) {
    	List<ClassFilter> filters = this.getClassFilters();
    	
        if (filters.isEmpty()) {
            return false;
        }
        for (ClassFilter classFilter : filters) {
            if (!classFilter.accept(clazz)) {
                return false;
            }
        }
        return true;
    }
    

}
