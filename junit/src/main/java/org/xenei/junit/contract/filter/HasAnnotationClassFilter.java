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
import java.lang.annotation.Annotation;

/**
 * Accepts classes that have the specified annotation.
 */
public class HasAnnotationClassFilter extends AbstractBaseClassFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4258956807308815129L;
	
	private Class<? extends Annotation> annotation;

	/**
	 * Constructor.
	 * 
	 * @param annotation The annotation for the class to have.
	 */
    public HasAnnotationClassFilter(Class<? extends Annotation> annotation) {
    	this.annotation = annotation;
    }

    /**
     * Checks to see if the class has the annotation..
     *
     * @param clazz  the Class to check
     * @return true if the class has the annotation.
     */
    @Override
    public boolean accept(Class<?> clazz) {
    	return null != clazz.getAnnotation(annotation);
    }

    /**
     * Checks to see if the class has the annotation..
     *
     * @param className  the class name to check
     * @return true if the class has the annotation.
     */
    @Override
    public boolean accept(String className) {
    	
    	try {
			return accept( Class.forName(className ));
		} catch (ClassNotFoundException e) {
			return false;
		}
    }

}