/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xenei.junit.contract.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation for ConditionalClassFilter implementations.
 * Provides implementations for basic add/delete filters and toString.
 *
 */
public abstract class AbstractConditionalClassFilter implements
		ConditionalClassFilter {

	/** The list of file filters. */
    private final List<ClassFilter> classFilters;
    
    protected AbstractConditionalClassFilter()
    {
    	classFilters = new ArrayList<ClassFilter>();
    }
    
    @Override
    public String[] args()
    {
    	String[] retval = new String[classFilters.size()];
    	for (int i=0;i<classFilters.size();i++)
    	{
    		retval[i] = classFilters.get(i).toString();
    	}
    	return retval;
    }

	@Override
	public Collection<Class<?>> filter(Collection<Class<?>> collection) {
		return ClassFilter.Util.filterClasses( collection,  this );
	}

	@Override
	public Collection<String> filterNames(Collection<String> collection) {
		return ClassFilter.Util.filterClassNames( collection,  this );
	}
    
    protected boolean isFilterListEmpty()
    {
    	return classFilters.isEmpty();
    }
    
    protected AbstractConditionalClassFilter(final List<ClassFilter> classFilters) {
        if (classFilters == null) {
            this.classFilters = new ArrayList<ClassFilter>();
        } else {
            this.classFilters = new ArrayList<ClassFilter>(classFilters);
        }
    }
    
    protected AbstractConditionalClassFilter(final ClassFilter... classFilters) {
    	this();
    	addClassFilters( classFilters );  
    }
    
	@Override
	public final void addClassFilter(ClassFilter classFilter) {
		if (classFilter == null)
    	{
    		throw new IllegalArgumentException( "classFilter may not be null");
    	}
        this.classFilters.add(classFilter);
	}

	@Override
	public final List<ClassFilter> getClassFilters() {
		return Collections.unmodifiableList(this.classFilters);
	}

	@Override
	public final boolean removeClassFilter(ClassFilter classFilter) {
		return this.classFilters.remove(classFilter);
	}

	@Override
	public final void setClassFilters(List<ClassFilter> classFilters) {
		this.classFilters.clear();
		addClassFilters( classFilters );
	}
	
	@Override
	public final void addClassFilters(List<ClassFilter> classFilters) {
        for (ClassFilter filter : classFilters)
        {
        	addClassFilter(filter);
        }

	}

	@Override
	public final void setClassFilters(ClassFilter... classFilters) {
		this.classFilters.clear();
		addClassFilters( classFilters );
	}
	
	
	@Override
	public final void addClassFilters(ClassFilter... classFilters) {
	    for (ClassFilter filter : classFilters)
        {
        	addClassFilter(filter);
        }
	}
	
	@Override
	public final void removeClassFilters(List<ClassFilter> classFilters) {
		this.classFilters.removeAll(classFilters); 
	}

	@Override
	public final void removeClassFilters(ClassFilter... classFilters) {
		this.classFilters.removeAll(Arrays.asList(classFilters)); 
	}
	
    
    @Override
    public String toString() {
        return ClassFilter.Util.toString( this );
    }
}
