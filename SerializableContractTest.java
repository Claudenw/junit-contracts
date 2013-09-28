
import org.junit.Assert.fail;

@Contract( Serializable.class )
public class SerializableContractTest() {

  private Serializable s;
  
  @Contract.Inject
  public void setSerializable( Serializable s )
  {
     this.s = s;
  }
  
  
  @Test
  public void testSerializableContract() {

			try {
				Method m = clz.getDeclaredMethod("writeObject",
						java.io.ObjectOutputStream.class);
				if (!Modifier.isPrivate(m.getModifiers())) {
					fail(String.format("writeObject in %s is not private", s.getClass() ));
				}
				m = clz.getMethod("readObject", java.io.ObjectInputStream.class);
				if (!Modifier.isPrivate(m.getModifiers())) {
					fail( String.format("readObject in %s is not private", s.getClass()));
				}
			} catch (NoSuchMethodException expected) {
				for (Field fld : clz.getDeclaredFields()) {
					if (!Modifier.isTransient(fld.getModifiers())
							&& !Modifier.isStatic(fld.getModifiers())) {
						Class<?> clazz = fld.getType();
						// not serializable and not a collection
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
						if (clazz != null && !clazz.isPrimitive()) {
							if (!Serializable.class.isAssignableFrom(clazz)) {
								fail(String
												.format(
														"field %s containing type %s is not serializable, static, or transient but class %s is serializable",
														fld.getName(), clazz.getName(), clz.getName()));
							}
						}
					}
				
			}
		}
	}
