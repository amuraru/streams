package stream.plugin.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.mapper.HideFeature;
import stream.util.EmbeddedContent;
import stream.util.Parameter;
import stream.util.ParameterDiscovery;
import stream.util.ParameterInjection;

import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeText;
import com.rapidminer.parameter.TextType;

/**
 * This class implements an annotation-based parameter-type discovery. This allows
 * for annotating class fields and automatically adding these fields to the RapidMiner
 * operator object. 
 * 
 * @author Christian Bockermann
 *
 */
public class ParameterTypeDiscovery {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( ParameterTypeDiscovery.class );


	/**
	 * Check the given class for any @parameter annotated fields.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<String,ParameterType> discoverParameterTypes( Class<?> clazz ){

		log.debug( "------------------------------------------------------------------------" );
		log.debug( "Exploring ParameterTypes for class '{}'", clazz );
		Map<String,ParameterType> types = new LinkedHashMap<String,ParameterType>();
		Field[] fields = clazz.getDeclaredFields();

		log.debug( "Found {} fields", fields.length );


		for( Field field : fields ){
			ParameterType type = null;
			Parameter param = field.getAnnotation( Parameter.class );
			if( param != null ){
				log.debug( "Found @parameter annotated field '{}'", field.getName() );
				log.debug( "    field.getType() = {}", field.getType() );

				type = getParameterType( param, field.getName(), field.getType() );
				/*
				if( field.getType().equals( String.class ) ){
					log.debug( "Field is a String" );

					if( param.values() != null ){
						type = new ParameterTypeString( param.name(), desc, !param.required() );
					} else {
						type = new ParameterTypeString( param.name(), desc, !param.required() );
					}
				}

				if( field.getType().equals( Integer.class ) || field.getType().equals( Long.class ) ){
					log.debug( "Field {} is an Integer!", field.getName() );
					type = new ParameterTypeInt( param.name(), desc, (new Double(param.min())).intValue(), (new Double(param.max())).intValue(), !param.required() );
				}

				if( field.getType().equals( Boolean.class ) ){
					log.debug( "Field {} is a Boolean!", field.getName() );
					type = new ParameterTypeBoolean( param.name(), desc, !param.required() );
				}
				 */


				if( type != null ){
					log.debug( "Adding new parameter-type {}", type );
					types.put( param.name(), type );
				} else {
					log.error( "Failed to properly determine annotated field {} in class {}", field.getName(), clazz.getName() );
				}

			} else {
				log.debug( "Field '{}' is not annotated as parameter", field.getName() );
			}
		}

		


		for( Method m : clazz.getMethods() ){
			
			log.trace( "Checking method {}", m );

			if( ParameterDiscovery.isSetter( m ) ){
				log.debug( "Found setter '{}'", m.getName() );
				String key = m.getName().substring( 3, 4 ).toLowerCase();
				
				if( types.containsKey( key ) ){
					log.debug( "Already have annotated field for key '{}', skipping setter {}", key, m );
					continue;
				}
				
				if( m.getName().length() > 4 )
					key += m.getName().substring( 4 );
				
				Parameter param = m.getAnnotation( Parameter.class );
				if( param != null && ! "".equals( param.name().trim() ) ){
					key = param.name();
					log.debug( "Setting parameter for method '{}' to key '{}'", m.getName(), key );
				}
				
				
				Class<?>[] t = m.getParameterTypes();
				if( t[0] == EmbeddedContent.class ){
					log.debug( "Found EmbeddedContent parameter, key = '{}'", key );
					ParameterType type = new ParameterTypeText( key, "", TextType.JAVA );
					types.put( key, type );
					continue;
				}
				
				ParameterType type = getParameterType( param, key, m.getParameterTypes()[0] );
				if( type != null ){
					log.debug( "Adding parameter-type: {}", type );
					types.put( key, type );
					log.debug( "  => parameter '{}'", key );
				}
			}
		}

		if( log.isDebugEnabled() ){
			
			for( String key : types.keySet() ){
				ParameterType type = types.get( key );
				log.debug( "  key '{}' => {}  (name: " + type.getKey() + ")", key, type );
			}
		}

		log.debug( "------------------------------------------------------------------------" );
		return types;
	}


	public static ParameterType getParameterType( Parameter param, String name, Class<?> type ){

		String desc = "";
		ParameterType pt = null;

		String key = name;
		if( param != null && param.name() != null && ! "".equals( param.name().trim() ) ){
			key = param.name();
		}
		
		if( param != null && param.description() != null ){
			desc = param.description();
		}
		

		if( type.equals( String.class ) ){
			log.debug( "ParameterType is a String" );

			if( param != null && param.values() != null ){
				pt = new ParameterTypeString( key, desc, !param.required() );
			} else {
				pt = new ParameterTypeString( key, desc, false );
			}
			if( param != null && param.defaultValue() != null )
				pt.setDefaultValue( param.defaultValue() );
			
			if( param != null && param.values() != null && param.values().length > 1 ){
				log.info( "Found category-parameter!" );
				ParameterTypeCategory cat = new ParameterTypeCategory( key, desc, param.values(), 0 );
				return cat;
			}
			
			return pt;
		}


		if( type.equals( Double.class ) ){
			log.debug( "ParameterType {} is a Double!" );
			if( param != null ){
				pt = new ParameterTypeDouble( key, desc, param.min(), param.max(), !param.required() );
			} else 
				pt = new ParameterTypeDouble( key, desc, Double.MIN_VALUE, Double.MAX_VALUE, 0.0d );
			if( param != null && param.defaultValue() != null ){
				pt.setDefaultValue( new Double( param.defaultValue() ) );
			}
			return pt;
		}

		
		if( type.equals( Integer.class ) || type.equals( Long.class ) ){

			log.debug( "ParameterType {} is an Integer!", type );
			if( param != null ){
				pt = new ParameterTypeInt( key, desc, (new Double(param.min())).intValue(), (new Double(param.max())).intValue(), !param.required() );
			} else {
				pt = new ParameterTypeInt( key, desc, 0, Integer.MAX_VALUE, true );
			}
			
			if( param != null && param.defaultValue() != null ){
				pt.setDefaultValue( new Integer( param.defaultValue() ) );
			}
			return pt;
		}

		if( type.equals( Boolean.class ) ){
			log.debug( "ParameterType {} is a Boolean!" );
			if( param != null )
				pt = new ParameterTypeBoolean( key, desc, !param.required() );
			else
				pt = new ParameterTypeBoolean( key, desc, true );
			if( param != null && param.defaultValue() != null ){
				pt.setDefaultValue( new Boolean( param.defaultValue() ) );
			}
			return pt;
		}
		
		if( type.equals( File.class ) ){
			pt = new ParameterTypeFile( key, desc, null, !param.required() );
			return pt;
		}

		
		if( Map.class.isAssignableFrom( type ) ){
			
			log.info( "Found Map parameter... " );
			pt = new ParameterTypeList( key, desc,
					new ParameterTypeString( "key", "" ),
					new ParameterTypeString( "value", "" )
					);
			return pt;
		}
		
		return pt;
	}


	public static void inject( Object object, Map<String,Object> parameters ){
		try {
			log.debug( "Using ParameterInjection to set parameters..." );
			ParameterInjection.inject( object, parameters );
		} catch (Exception e) {
			log.error( "Failed to inject parameters: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}
	}


	public static void main( String[] args ) throws Exception {

		Map<String,Object> params = new HashMap<String,Object>();
		params.put( "startId", new Long( 100L ) );
		params.put( "key", "x" );
		HideFeature proc = new HideFeature();


		ParameterInjection.inject( proc, params );

		Data datum = new DataImpl();
		datum.put( "x", 1.0d );
		datum.put( "y", 2.10d);

		log.debug( "Initial datum: {}", datum );
		datum = proc.process( datum );
		log.debug( "Processed datum: {}", datum );
		
		
		Map<String,ParameterType> types = discoverParameterTypes( stream.data.mapper.MapKeys.class );
		for( String key : types.keySet() ){
			log.info( "Found '{}' = {}", key, types.get( key ) );
		}

		log.info( "------------------------------------------------------------------" );
		
		types = discoverParameterTypes( stream.data.mapper.CreateID.class );
		for( String key : types.keySet() ){
			log.info( "Found '{}' = {}", key, types.get( key ) );
		}
	
		log.info( "------------------------------------------------------------------" );
		
		types = discoverParameterTypes( stream.data.mapper.MapValueToID.class );
		for( String key : types.keySet() ){
			log.info( "Found '{}' = {}", key, types.get( key ) );
		}
	
	}
}