package stream.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.mapper.HideFeature;

/**
 * This class implements an annotation-based parameter-type discovery. This allows
 * for annotating class fields and automatically adding these fields to the RapidMiner
 * operator object. 
 * 
 * @author Christian Bockermann
 *
 */
public class ParameterDiscovery {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( ParameterDiscovery.class );


	/**
	 * Check the given class for any @parameter annotated fields.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<String,Class<?>> discoverParameters( Class<?> clazz ){

		Map<String,Class<?>> types = new LinkedHashMap<String,Class<?>>();
		Field[] fields = clazz.getDeclaredFields();

		log.info( "Found {} fields", fields.length );


		for( Field field : fields ){
			Parameter param = field.getAnnotation( Parameter.class );
			if( param != null ){
				log.info( "Found @parameter annotated field '{}'", field.getName() );
				log.info( "    field.getType() = {}", field.getType() );
				types.put( param.name(), field.getType() );
			} else {
				log.info( "Field '{}' is not annotated as parameter", field.getName() );
			}
		}


		for( Method m : clazz.getMethods() ){

			if( ParameterDiscovery.isSetter( m ) ){
				log.info( "Found getter '{}'", m.getName() );
				String key = m.getName().substring( 3, 4).toLowerCase();
				if( m.getName().length() > 4 )
					key += m.getName().substring( 4 );
				
				if( ! types.containsKey( key ) ){
					log.info( "  => parameter '{}'", key );
					types.put( key, m.getReturnType() );
				} else
					log.info( "Parameter {} already defined by annotation", key );
			}
		}

		return types;
	}

	
	/**
	 * This method returns the parameter annotation from the given class for the specified
	 * parameter key. If attribute has been annotated with the given key as name, then this
	 * method will return <code>null</code>.
	 * 
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static Parameter getParameterAnnotation( Class<?> clazz, String key ){

		Field[] fields = clazz.getDeclaredFields();

		log.info( "Found {} fields", fields.length );

		for( Field field : fields ){
			Parameter param = field.getAnnotation( Parameter.class );
			if( param != null && ( param.name().equals( key ) ) || field.getName().equals( key ) ){
				log.info( "Found @parameter annotated field '{}'", field.getName() );
				log.info( "    field.getType() = {}", field.getType() );
				return param;
			} else {
				log.info( "Field '{}' is not annotated as parameter", field.getName() );
			}
		}
		
		return null;
	}
	
	
	
	public static List<Parameter> discoverParameterAnnotations( Class<?> clazz ){
		List<Parameter> parameters = new ArrayList<Parameter>();
		Field[] fields = clazz.getDeclaredFields();

		log.info( "Found {} fields", fields.length );

		for( Field field : fields ){
			Parameter param = field.getAnnotation( Parameter.class );
			if( param != null ){
				log.info( "Found @parameter annotated field '{}'", field.getName() );
				log.info( "    field.getType() = {}", field.getType() );
				parameters.add( param );
			} else {
				log.info( "Field '{}' is not annotated as parameter", field.getName() );
			}
		}
		return parameters;
	}

	
	
	public static String getParameterName( Method m ){
		if( isGetter( m ) ){
		
			String key = m.getName().substring( 3, 4 ).toLowerCase();
			if( m.getName().length() > 3 )
				key += m.getName().substring( 4 );
			
			return key;
		}
		
		return null;
	}
	
	

	/**
	 * This method defines whether a method matches all requirements of being a get-Method. Basically
	 * this requires the name to start with <code>get</code> and the return type to be any of the
	 * ones supported for injection.
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isGetter( Method m ){
		if( m.getName().toLowerCase().startsWith( "get" ) ){
			Class<?> rt = m.getReturnType();
			if( ParameterInjection.isTypeSupported( rt ) )
				return true;
		}
		return false;
	}


	/**
	 * This method defines whether a method matches all requirements of being a get-Method. Basically
	 * this requires the name to start with <code>set</code> and the single (!) parameter type to be 
	 * any of the ones supported for injection.
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isSetter( Method m ){
		if( m.getName().toLowerCase().startsWith( "get" ) && m.getParameterTypes().length == 1 ){
			Class<?> rt = m.getParameterTypes()[0];
			if( ParameterInjection.isTypeSupported( rt ) )
				return true;
		}
		return false;
	}
	

	public static void main( String[] args ) throws Exception {

		Map<String,Object> params = new HashMap<String,Object>();
		params.put( "startId", new Long( 100L ) );
		params.put( "key", "x" );
		HideFeature proc = new HideFeature();


		ParameterDiscovery.discoverParameters( proc.getClass() );

		ParameterInjection.inject( proc, params );

		Data datum = new DataImpl();
		datum.put( "x", 1.0d );
		datum.put( "y", 2.10d);

		log.info( "Initial datum: {}", datum );
		datum = proc.process( datum );
		log.info( "Processed datum: {}", datum );
	}
}