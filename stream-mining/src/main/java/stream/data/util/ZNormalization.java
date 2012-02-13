/**
 * 
 */
package stream.data.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.learner.LearnerUtils;

/**
 * @author chris
 *
 */
public class ZNormalization {

	static Logger log = LoggerFactory.getLogger( ZNormalization.class );
	
	
	public static void normalize( Collection<Data> input ){

		Map<String,Double> avg = new HashMap<String,Double>();
		Map<String,Double> std = new HashMap<String,Double>();
		
		log.info( "Computing average..." );
		for( Data item : input ){
			for( String key : LearnerUtils.getNumericAttributes( item ) ){
				
				Double sum = avg.get( key );
				if( sum == null )
					sum = 0.0d;
				
				sum += new Double( item.get(key).toString() );
				avg.put( key, sum );
			}
		}
		
		for( String key : avg.keySet() ){
			avg.put( key, avg.get( key ) / input.size() );
		}
		
		//
		// compute standard deviations
		//
		log.info( "Computing standard deviations..." );
		for( Data item : input ){
			
			for( String key : avg.keySet() ){
				Double sum = std.get(key);
				if( sum == null )
					sum = 0.0d;
				
				Double m = avg.get(key);
				Double val = new Double( item.get(key).toString() );
				
				Double d = (val - m) * (val - m);
				std.put( key, sum + d );
			}
		}
		
		for( String key : std.keySet() ){
			Double d = std.get(key);
			std.put( key, Math.sqrt( d ) );
		}
	
		log.info( "Normalizing data..." );
		for( Data item : input ){
			for( String key : avg.keySet() ){
				Double value = new Double( item.get( key ).toString() );
				if( value != null ){
					value = ( value - avg.get(key) ) / std.get( key );
					item.put( key, value );
				}
			}
		}
	}
}