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
 */
@Contract(Serializable.class)
public abstract class SerializableContractTest {

	@Contract.Inject
	protected abstract IProducer<Serializable> getProducer();

	@Test
	public void testSerializableContract() {
		Serializable s = getProducer().newInstance();
		Class<?> clz = s.getClass();
		boolean hasSpecialMethods = false;
		/*
		 * From serialization javadoc Classes that require special handling
		 * during the serialization and deserialization process must implement
		 * special methods with these exact signatures: private void
		 * writeObject(java.io.ObjectOutputStream out) throws IOException
		 * private void readObject(java.io.ObjectInputStream in) throws
		 * IOException, ClassNotFoundException; private void readObjectNoData()
		 * throws ObjectStreamException;
		 */
		try {
			Method m = clz.getDeclaredMethod("writeObject",
					java.io.ObjectOutputStream.class);
			hasSpecialMethods = true;
			if (!Modifier.isPrivate(m.getModifiers())) {
				fail(String.format("writeObject in %s is not private", clz));
			}
			m = clz.getMethod("readObject", java.io.ObjectInputStream.class);
			if (!Modifier.isPrivate(m.getModifiers())) {
				fail(String.format("readObject in %s is not private", clz));
			}
			// prior to version 1.4 of the JavaTM 2 SDK, Standard Edition,
			// there was no support for readObjectNoData

			if (getJavaVersion() >= 1.4d) {
				m = clz.getMethod("readObjectNoData");
				if (!Modifier.isPrivate(m.getModifiers())) {
					fail(String.format("readObjectNoData in %s is not private",
							clz));
				}
			}
		} catch (NoSuchMethodException expected) {

			if (hasSpecialMethods) {
				StringBuilder sb = new StringBuilder()
						.append("Must define all or none of the following methods: ")
						.append("private void writeObject(java.io.ObjectOutputStream out) throws IOException")
						.append(" / ")
						.append("private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException");

				if (getJavaVersion() >= 1.4d) {
					sb.append(" / ")
							.append("private void readObjectNoData() throws ObjectStreamException");
				}
				fail(sb.toString());
			}
			// check that all fields meet the serialization requirements
			for (Field fld : clz.getDeclaredFields()) {

				/*
				 * Transient and static fields do not need to be serialized
				 * so skip them
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

	// get the java version as a double.
	private double getJavaVersion() {
		String version = System.getProperty("java.version");
		int pos = 0, count = 0;
		for (; pos < version.length() && count < 2; pos++) {
			if (version.charAt(pos) == '.')
				count++;
		}
		return Double.parseDouble(version.substring(0, pos));
	}
}
