/**
 * 
 */
package stream.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class provides some utility methods for injecting parameters into an object
 * by the use of Java's reflection API.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class ParameterInjection {

	/* A global logger for this class */
	static Logger log = LoggerFactory.getLogger( ParameterInjection.class );


	/**
	 * This method injects a set of parameters to the given object.
	 * 
	 * @param o      The object to inject parameters into.
	 * @param params The parameters to set on the object.
	 * @throws Exception
	 */
	public static void inject( Object o, Map<String,?> params ) throws Exception {
		log.debug( "Injecting parameters {} into object {}", params, o );

		// the class of this object
		Class<?> clazz = o.getClass();

		// this set contains a list of parameters that have been successfully set using
		// accessible fields
		//
		Set<String> alreadySet = new HashSet<String>();

		
		// first we try to directly set the annotated field of the class. this may fail if
		// the field has private or protected access
		//
		for( String k : params.keySet() ){
			try {
				Field field = clazz.getDeclaredField( k );
				if( field != null && field.isAccessible() && field.isAnnotationPresent( Parameter.class ) ){
					log.info( "Found accessible field {} for class {}", field.getName(), o.getClass() );
					field.set( o, params.get( k ) );
					alreadySet.add( k );
				}
			} catch (NoSuchFieldException nsfe) {
				log.warn( "Object of class {} does not provide a field for key '{}'", clazz, k );
				if( log.isTraceEnabled() )
					nsfe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		// now, walk over all methods and check if one of these is a setter of a corresponding
		// key value in the parameter map
		//
		for( Method m : o.getClass().getMethods() ){
			for( String k : params.keySet() ){

				if( m.getName().startsWith( "set" ) && alreadySet.contains( k ) ){
					log.info( "Skipping setter '{}' for already injected field {}", m.getName(), k );
					continue;
				}

				//
				// if the method corresponds to a parameter of the map, try to call it
				// with the appropriate value
				//
				if( m.getName().equalsIgnoreCase( "set" + k ) && m.getParameterTypes().length == 1 ){

					Class<?> t = m.getParameterTypes()[0];

					if( t.equals( params.get( k ).getClass() ) ){
						log.info( "Using setter '{}' to inject parameter '{}'", m.getName(), k );
						//
						// if the setter's argument type matches the value object's class
						// in the parameter-map, we simply inject that object
						//
						m.invoke( o, params.get( k ) );

					} else {
						//
						// if the setter's argument does NOT match, we try to create a new,
						// appropriate value for that setter using the string-constructor
						// of the setter's argument type class
						//
						try {
							Constructor<?> c = t.getConstructor( String.class );
							Object po = c.newInstance( params.get( k ).toString() );
							log.debug( "Invoking {}({})", m.getName(), po );
							m.invoke( o, po );
						} catch (NoSuchMethodException nsm ){
							log.error( "No String-constructor found for type {} of method {}", t, m.getName() );
						}
					}
				}
			}
		}
	}


	public static Map<String,String> extract( Object learner ) throws Exception {
		Map<String,String> params = new TreeMap<String,String>();
		for( Method m : learner.getClass().getMethods() ){
			if( m.getName().startsWith( "get" ) && m.getParameterTypes().length == 0 ){
				log.trace( "Found getter '{}' for class '{}'", m.getName(), learner.getClass() );
				Class<?> rt = m.getReturnType();
				if( isTypeSupported( rt ) ) { // rt.isPrimitive() || rt.equals( String.class ) || rt.equals( Double.class ) ){
					Object val = m.invoke( learner, new Object[0] );
					String key = ParameterDiscovery.getParameterName( m );
					if( key != null )
						params.put( key, "" + val );
					else
						log.warn( "Failed to detect parameter name from method '{}'! Skipping that method.", m.getName() );
				}
			}
		}
		return params;
	}
	
	
	public static boolean isGetter( Method m ){
		return ParameterDiscovery.isGetter( m );
	}
	
	
	public static boolean hasGetter( Class<?> clazz, String name ) {
		try {
			for( Method m : clazz.getMethods() ){
				if( isGetter( m ) && m.getName().equalsIgnoreCase( "get" + name ) )
					return true;
			}
		} catch (Exception e){
		}
		return false;
	}
	
	
	public static boolean isTypeSupported( Class<?> clazz ){
		if( clazz.equals( String.class )
				|| clazz.equals( Long.class )
				|| clazz.equals( Integer.class ) 
				|| clazz.equals( Double.class ) 
				|| clazz.equals( Boolean.class ) )
			return true;
		
		if( clazz.isPrimitive() )
			return true;
		
		return false;
	}
}