package org.xenei.junit.contract;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classes run with ContractSuite must have a @Contract.Inject method that creates an instance of the
 * class under test. It must extend a test annotated with @Contract and that implementation must include
 * a method with the @Contract.Inject annotation.
 *
 */
public class ContractSuite extends ParentRunner<Runner> {
	private static final Logger LOG = LoggerFactory.getLogger(ContractSuite.class);
	 private final List<Runner> fRunners;

	 
	/**
     * Called reflectively on classes annotated with <code>@RunWith(Suite.class)</code>
     *
     * @param klass the root class
     * @param builder builds runners for classes in the suite
	 * @throws Throwable 
     */
    public ContractSuite(Class<?> klass, RunnerBuilder builder) throws Throwable {
    	 super(klass);
    	 
    	 Map<Class<?>,Method> testToMethodMap = new HashMap<Class<?>,Method>();
    	 Path tempDir = null;
    	 JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    	 StandardJavaFileManager manager = compiler.getStandardFileManager(null,
 				null, null);
 		
 		
    	 try {	
    	 Method getter = getInjectibleGetterMethod( klass );
    	 Object baseObj = klass.newInstance();
    	 // add a runner for base class that handles any @Test annotations
    	 List<Runner> r = new ArrayList<Runner>();
    	  	 
    	 // get all the annotated classes and add them as well.
    	 for (Class<?> c : getAnnotatedClasses(klass, testToMethodMap))
    	 {
    		 if (Modifier.isAbstract(c.getModifiers()))
    		 {
    			 if (tempDir == null)
    			 {
    				 tempDir = Files.createTempDirectory("JUC_");
    				 manager.setLocation(StandardLocation.CLASS_OUTPUT,
    			 				Arrays.asList(new File[] { tempDir.toFile() }));
    		    	 addPath( tempDir );
    			 }
    			 Class<?> wrapperClass = wrapClass( c, compiler, manager  );
    			 r.add( new ContractTestRunner( wrapperClass,  testToMethodMap.get( c ), baseObj, getter ));
    		 }
    		 else
    		 {
    			 r.add( builder.runnerForClass( c ));
    		 }
    	 }		
    	 fRunners = Collections.unmodifiableList(r);
    	 }
  		finally {
  			if (manager != null) {
  				manager.close();
  			}
  		}
  		
    }
  
    private static Class<?> wrapClass( Class<?> origClass, JavaCompiler compiler, StandardJavaFileManager manager) throws ClassNotFoundException, IOException
    {
    	String fqName = origClass.getCanonicalName()+"_Wrapped";
    	
    	String source = String.format("package %1$s;class %2$s_Wrapped extends %3$s { public %2$s_Wrapped(){} }",
    		origClass.getPackage().getName(),
    		origClass.getSimpleName(),
    		origClass.getCanonicalName());
    	
    	JavaSourceFromString[] compList = { new JavaSourceFromString(fqName,
				source) };
    	
    	CompilationTask task = compiler.getTask(null, manager, null, null,
				null, Arrays.asList(compList));
		Boolean result = task.call();
		return result ? Class.forName(fqName) : null;
		
    }
    
	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner child, RunNotifier notifier) {
		child.run( notifier);
	}
	
	/**
	 * 
	 * @param klass A class annotated with @RunWith( ContractSuite.class )  Is not abstract
	 * @return
	 * @throws InitializationError
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Class<?>[] getAnnotatedClasses(Class<?> klass,Map<Class<?>,Method> testToMethodMap) throws InitializationError, ClassNotFoundException, IOException {

        // get all the classes that are Contract tests and the methods that set
        // the class under test.
        Map<Class<?>,Class<?>> implToTestMap = new HashMap<Class<?>,Class<?>>();
       
        
        
        for (Class<?> clazz : ClassPathUtils.getClasses(""))
        {
        	// contract annotation is on the test class
        	// value of contract annotation is class under test
        	Contract c = clazz.getAnnotation(Contract.class);
        	if (c != null)
        	{
        	  Method m = getInjectibleSetterMethod( clazz, c.value() );
        	  testToMethodMap.put( clazz,  m);
        		Class<?> oldVal = implToTestMap.put( c.value(), clazz  );
        		
        		if (oldVal != null)
        		{
        			LOG.warn( String.format("Multiple tests for class %s: replacing %s with %s", c.value(), oldVal, clazz));
        		}
        		
        	}
        }
        
        
        // The class we want to scan has a base class that is a test for a class.
        Class<?> contractTestClass = klass.getSuperclass();
      Contract annotation = contractTestClass.getAnnotation(Contract.class);
      if (annotation == null) {
    	  throw new IllegalStateException( "Classes annotated as @RunWith( ContractSuite ) must be derived from an  @Contract annotation");
      }
      Class<?> baseTestClass = annotation.value();
        
        // list of test classes
        Set<Class<?>> testClasses = new HashSet<Class<?>>();
        
        // list of implementation classes
        Set<Class<?>> implClasses = new HashSet<Class<?>>();
        implClasses.addAll( Arrays.asList(baseTestClass.getClasses()));
        implClasses.addAll( Arrays.asList(baseTestClass.getInterfaces()));
        implClasses.add( baseTestClass.getSuperclass() );
        implClasses.add( baseTestClass );
        implClasses.remove(null);
        Iterator<Class<?>> iter = implClasses.iterator();
        while (iter.hasNext())
        {
        	Class<?> testClass = implToTestMap.get( iter.next() );
        	if (testClass != null)
        	{
        		testClasses.add(testClass);
        	}
        }
        return testClasses.toArray( new Class<?>[testClasses.size()]);
    }
	
	private Method getInjectibleGetterMethod(Class<?> klass)
	{
	  for (Method m : klass.getDeclaredMethods())
	  {
	    if (m.getAnnotation(Contract.Inject.class) != null)
	    {
	      if (! m.getReturnType().equals( Void.TYPE ))
	      {
	        return m;
	      }
	    }
	  }
	  throw new IllegalStateException( "Classes annotated as @RunWith( ContractSuite ) ("+klass+") must include a @Contract.Inject annotation on a declared getter method");
	}
	
	private static Method getInjectibleSetterMethod(Class<?> klass, Class<?> arg)
  {
    
    for (Method m : klass.getDeclaredMethods())
    {
      if (m.getAnnotation(Contract.Inject.class) != null)
      {
        Class<?> params[] = m.getParameterTypes();
        if (params.length == 1 && params[0].equals( arg ))
        {
          return m;
        }
      }
    }
    throw new IllegalStateException( "Classes annotated as @Contract( X ) must include a @Contract.Inject annotation on a declared setter method with a single X as the parameter");
  }
	
	// add path to class loader
	private static void addPath(Path s) throws Exception {
	    File f = s.toFile();
	    f.mkdir();
	    URL u = f.toURI().toURL();
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class<?> urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, new Object[]{u});
	}
	
	/**
     * The <code>Contracts</code> annotation specifies the classes to be run when a class
     * annotated with <code>@RunWith(ContractSuite.class)</code> is run.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface Contracts {
        /**
         * @return the classes to be run
         */
        public Class<?>[] value();
    }
    
    /**
	 * A file object used to represent source coming from a string.
	 */
	public static class JavaSourceFromString extends SimpleJavaFileObject {
		/**
		 * The source code of this "file".
		 */
		final String code;

		/**
		 * Constructs a new JavaSourceFromString.
		 * 
		 * @param name
		 *            the name of the compilation unit represented by this file
		 *            object
		 * @param code
		 *            the source code for the compilation unit represented by
		 *            this file object
		 */
		JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}
}
