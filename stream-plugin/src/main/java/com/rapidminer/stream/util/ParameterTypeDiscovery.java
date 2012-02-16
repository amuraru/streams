package com.rapidminer.stream.util;

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
import stream.util.Parameter;
import stream.util.ParameterDiscovery;
import stream.util.ParameterInjection;

import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;

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
			
			log.debug( "Checking method {}", m );

			if( ParameterDiscovery.isSetter( m ) ){
				log.debug( "Found setter '{}'", m.getName() );
				String key = m.getName().substring( 3, 4).toLowerCase();
				
				if( types.containsKey( key ) ){
					log.debug( "Already have annotated field for key '{}', skipping setter {}", key, m );
					continue;
				}
				
				if( m.getName().length() > 4 )
					key += m.getName().substring( 4 );
				
				Parameter param = m.getAnnotation( Parameter.class );
				ParameterType type = getParameterType( param, key, m.getParameterTypes()[0] );
				if( type != null ){
					log.debug( "Adding parameter-type: {}", type );
					types.put( key, type );
					log.debug( "  => parameter '{}'", key );
				}
			}
		}


		return types;
	}


	public static ParameterType getParameterType( Parameter param, String name, Class<?> type ){

		String desc = "";
		ParameterType pt = null;

		if( type.equals( String.class ) ){
			log.debug( "ParameterType is a String" );

			if( param != null && param.values() != null ){
				pt = new ParameterTypeString( param.name(), desc, !param.required() );
			} else {
				pt = new ParameterTypeString( name, desc, false );
			}
			if( param != null && param.defaultValue() != null )
				pt.setDefaultValue( param.defaultValue() );
			
			if( param != null && param.values() != null && param.values().length > 1 ){
				log.info( "Found category-parameter!" );
				ParameterTypeCategory cat = new ParameterTypeCategory( name, desc, param.values(), 0 );
				return cat;
			}
			
			return pt;
		}


		if( type.equals( Double.class ) ){
			log.debug( "ParameterType {} is a Double!" );
			if( param != null ){
				pt = new ParameterTypeDouble( param.name(), desc, param.min(), param.max(), !param.required() );
			} else 
				pt = new ParameterTypeDouble( name, desc, Double.MIN_VALUE, Double.MAX_VALUE, 0.0d );
			if( param != null && param.defaultValue() != null ){
				pt.setDefaultValue( new Double( param.defaultValue() ) );
			}
			return pt;
		}

		
		if( type.equals( Integer.class ) || type.equals( Long.class ) ){

			log.debug( "ParameterType {} is an Integer!", type );
			if( param != null ){
				pt = new ParameterTypeInt( param.name(), desc, (new Double(param.min())).intValue(), (new Double(param.max())).intValue(), !param.required() );
			} else {
				pt = new ParameterTypeInt( name, desc, 0, Integer.MAX_VALUE, true );
			}
			
			if( param != null && param.defaultValue() != null ){
				pt.setDefaultValue( new Integer( param.defaultValue() ) );
			}
			return pt;
		}

		if( type.equals( Boolean.class ) ){
			log.debug( "ParameterType {} is a Boolean!" );
			if( param != null )
				pt = new ParameterTypeBoolean( param.name(), desc, !param.required() );
			else
				pt = new ParameterTypeBoolean( name, desc, true );
			if( param != null && param.defaultValue() != null ){
				pt.setDefaultValue( new Boolean( param.defaultValue() ) );
			}
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
	}
}