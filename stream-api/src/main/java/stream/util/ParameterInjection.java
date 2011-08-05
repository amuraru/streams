/**
 * 
 */
package stream.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
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


	/*
	public static void inject( Object o, Map<String,String> params ) throws Exception {
		log.debug( "Injecting parameters {} into object {}", params, o );

		for( Method m : o.getClass().getMethods() ){
			for( String k : params.keySet() ){
				//
				// if the method corresponds to a parameter of the map, try to call it
				// with the appropriate value
				//
				if( m.getName().equalsIgnoreCase( "set" + k ) && m.getParameterTypes().length == 1 ){
					Class<?> t = m.getParameterTypes()[0];
					try {
						Constructor<?> c = t.getConstructor( String.class );
						Object po = c.newInstance( params.get( k ) );
						log.debug( "Invoking {}({})", m.getName(), po );
						m.invoke( o, po );
					} catch (NoSuchMethodException nsm ){
						log.error( "No String-constructor found for type {} of method {}", t, m.getName() );
					}
				}
			}
		}
	}
	 */


	/**
	 * This method injects a set of parameters to the given object.
	 * 
	 * @param o      The object to inject parameters into.
	 * @param params The parameters to set on the object.
	 * @throws Exception
	 */
	public static void inject( Object o, Map<String,?> params ) throws Exception {
		log.debug( "Injecting parameters {} into object {}", params, o );

		for( Method m : o.getClass().getMethods() ){
			for( String k : params.keySet() ){
				//
				// if the method corresponds to a parameter of the map, try to call it
				// with the appropriate value
				//
				if( m.getName().equalsIgnoreCase( "set" + k ) && m.getParameterTypes().length == 1 ){

					Class<?> t = m.getParameterTypes()[0];

					if( t.equals( params.get( k ).getClass() ) ){
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
				if( rt.isPrimitive() || rt.equals( String.class ) || rt.equals( Double.class ) ){
					Object val = m.invoke( learner, new Object[0] );
					params.put( m.getName().substring(3,4).toLowerCase() + m.getName().substring( 4 ), "" + val );
				}
			}
		}
		return params;
	}
}