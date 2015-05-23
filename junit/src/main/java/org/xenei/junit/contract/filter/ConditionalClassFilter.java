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

import java.util.List;

/**
 * Defines operations for conditional class filters.
 *
 */
public interface ConditionalClassFilter extends ClassFilter {

    /**
     * Adds the specified file filter to the list of file filters at the end of
     * the list.
     *
     * @param classFilter the filter to be added
     */
    void addClassFilter(ClassFilter classFilter);

    /**
     * Returns this conditional file filter's list of file filters.
     *
     * @return the file filter list
     */
    List<ClassFilter> getClassFilters();

    /**
     * Removes the specified file filter.
     *
     * @param classFilter filter to be removed
     * @return {@code true} if the filter was found in the list,
     * {@code false} otherwise
     */
    boolean removeClassFilter(ClassFilter classFilter);

    /**
     * Sets the list of file filters, replacing any previously configured
     * file filters on this filter.
     *
     * @param classFilters the list of filters
     */
    void setClassFilters(List<ClassFilter> classFilters);
    
    /**
     * Sets the list of file filters, replacing any previously configured
     * file filters on this filter.
     *
     * @param classFilters the array of filters
     */
	void setClassFilters(ClassFilter... classFilters);
	
	 /**
     * Removes class filters from this filter.
     *
     * @param classFilters the list of filters to remove.
     */
	void removeClassFilters(List<ClassFilter> classFilters);

	/**
     * Removes class filters from this filter.
     *
     * @param classFilters the array of filters to remove.
     */
	void removeClassFilters(ClassFilter... classFilters);
	
	/**
     * Adds class filters to this filter.
     *
     * @param classFilters the list of filters to add.
     */
	void addClassFilters(List<ClassFilter> classFilters);

	/**
     * Adds class filters to this filter.
     *
     * @param classFilters the array of filters to add.
     */
	void addClassFilters(ClassFilter... classFilters);

}
