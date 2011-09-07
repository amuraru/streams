/**
 * 
 */
package stream.learner.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chris
 *
 */
public class VectorMath {

	
	
	
	
	public static Map<String,Double> add( Map<String,Double> x1, Map<String,Double> x2 ){
		return add( x1, 1.0d, x2 );
	}
	
	
	public static Map<String,Double> add( Map<String,Double> x1, double factor, Map<String,Double> x2 ){
		Map<String,Double> sum = new HashMap<String,Double>( x1 );
		
		for( String key : x2.keySet() ){
			Double d = sum.get( key );
			if( d == null )
				d = 0.0d;
			
			sum.put( key, d + ( factor * x2.get( key ) ) );
		}
		
		return sum;
	}
}