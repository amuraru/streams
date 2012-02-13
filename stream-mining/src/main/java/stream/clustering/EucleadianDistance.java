/**
 * 
 */
package stream.clustering;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import stream.data.Data;
import stream.learner.Distance;
import stream.learner.LearnerUtils;

/**
 * @author chris
 *
 */
public class EucleadianDistance implements Distance {

	/**
	 * @see stream.learner.Distance#distance(stream.data.Data, stream.data.Data)
	 */
	@Override
	public Double distance(Data x1, Data x2) {
		
		
		Map<String,Double> v1 = LearnerUtils.getNumericVector( x1 );
		Map<String,Double> v2 = LearnerUtils.getNumericVector( x2 );
		
		
		Set<String> keys = new HashSet<String>( v1.keySet() );
		keys.addAll( v2.keySet() );
		
		double dist = 0.0d;
		
		for( String key : keys ){
			
			Double d1 = v1.get( key );
			if( d1 == null )
				d1 = 0.0d;
			
			Double d2 = v2.get( key );
			if( d2 == null )
				d2 = 0.0d;
			
			double diff = ( d1 - d2 );
			dist +=  (diff * diff);
		}
		
		return Math.sqrt( dist );
	}
}