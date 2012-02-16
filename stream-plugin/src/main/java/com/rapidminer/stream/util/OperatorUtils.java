/**
 * 
 */
package com.rapidminer.stream.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidminer.operator.Operator;
import com.rapidminer.parameter.Parameters;

/**
 * @author chris
 *
 */
public class OperatorUtils {

	static Logger log = LoggerFactory.getLogger( OperatorUtils.class );
	
	
	public static Map<String,String> getParameters( Operator op ) throws Exception {
		Map<String,String> parameters = new LinkedHashMap<String,String>();
		
		Parameters params = op.getParameters();
		
		for( String key : params.getKeys() ){
			try {
				String value = params.getParameter( key );
				if( value != null ){
					parameters.put( key, value );
				}
			} catch (Exception e) {
				log.error( "Failed to get parameter '{}' from operator {}", key, op );
			}
		}
		
		return parameters;
	}
}
