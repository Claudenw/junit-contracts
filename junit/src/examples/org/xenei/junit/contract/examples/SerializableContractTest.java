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

package org.xenei.junit.contract.examples;

import static org.junit.Assert.fail;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.junit.Test;
import org.xenei.junit.contract.Contract;

import org.xenei.junit.contract.IProducer;

/**
 * verify that Serializable classes only contain serializable elements or
 * implement the special handling methods as per the Serializer javadoc.
 * 
 * From serialization javadoc Classes that require special handling during the
 * serialization and deserialization process must implement special methods with
 * these exact signatures: private void writeObject(java.io.ObjectOutputStream
 * out) throws IOException private void readObject(java.io.ObjectInputStream in)
 * throws IOException, ClassNotFoundException; private void readObjectNoData()
 * throws ObjectStreamException;
 * 
 */
@Contract(Serializable.class)
public class SerializableContractTest<T extends Serializable> {
	
	private IProducer<T> producer;
	
	public SerializableContractTest() {
		producer = new IProducer<T>(){

			@Override
			public T newInstance() {
				return new Integer(5);
			}

			@Override
			public void cleanUp() {
				// TODO Auto-generated method stub
				
			}};
	}
	
	
	
	@Contract.Inject
	public final void setProducer(IProducer<T> producer)
	{
		this.producer = producer;
	}
	
	protected final IProducer<T> getProducer()
	{
		return producer;
	}

	
	/**
	 * Test that the serializable contract is met.
	 * 
	 * This is a rather complex test and is not indicitive of 
	 * contract test complexity.  However, it does meet the 
	 * serializable contract.
	 */
	@ContractTest
	public void testSerializableContract() {
		Serializable s = getProducer().newInstance();
		Class<?> clz = s.getClass();
		boolean hasSpecialMethods = false;
		/*
		
		 */
		try {
			Method m = null;
			NoSuchMethodException hasException = null;
			try {
				m = clz.getDeclaredMethod("writeObject",
						java.io.ObjectOutputStream.class);
				
				hasSpecialMethods = true;			
				if (!Modifier.isPrivate(m.getModifiers())) {
					fail(String.format("writeObject in %s is not private", clz));
				}
			}
			catch (NoSuchMethodException expected) {
				hasException = expected;
			}
			
			m = clz.getMethod("readObject", java.io.ObjectInputStream.class);
			if (!Modifier.isPrivate(m.getModifiers())) {
				fail(String.format("readObject in %s is not private", clz));
			} 
			if (hasException != null)
			{
				// there was an exception so throw it.
				throw hasException;
			}
			
		} catch (NoSuchMethodException expected) {
			// if there was one method there must be both
			if (hasSpecialMethods) {
				StringBuilder sb = new StringBuilder()
						.append("Must define all or none of the following methods: ")
						.append("private void writeObject(java.io.ObjectOutputStream out) throws IOException")
						.append(" / ")
						.append("private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException");

				fail(sb.toString());
			}
			// check that all fields meet the serialization requirements
			for (Field fld : clz.getDeclaredFields()) {

				/*
				 * Transient and static fields do not need to be serialized so
				 * skip them
				 */
				if (!Modifier.isTransient(fld.getModifiers())
						&& !Modifier.isStatic(fld.getModifiers())) {
					// check the field type
					Class<?> clazz = fld.getType();
					// handle collections
					if (Collection.class.isAssignableFrom(clazz)) {
						// extract the class type from the collection
						String sig = fld.toGenericString();
						String[] parts = sig.split("[\\<\\>]");
						if (parts.length == 3) {
							try {
								clazz = Class.forName(parts[1]);
							} catch (ClassNotFoundException e) {
								clazz = null;
							}
						}

					}
					// null and primitive types serialize
					if (clazz != null && !clazz.isPrimitive()) {
						// if it is not serializable we have an error
						if (!Serializable.class.isAssignableFrom(clazz)) {
							fail(String
									.format("field %s containing type %s is not serializable, static, or transient but class %s is serializable",
											fld.getName(), clazz.getName(),
											clz.getName()));
						}
					}
				}
			}
		}
	}

}
