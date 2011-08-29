package stream.learner;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataUtils;


/**
 * The most common distance function.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class EucleadianDistance implements Distance {

	static Logger log = LoggerFactory.getLogger( EucleadianDistance.class );
	
	
	/**
	 * @see stream.learner.Distance#distance(stream.data.Data, stream.data.Data)
	 */
	@Override
	public Double distance(Data x1, Data x2) {

		Set<String> keys = LearnerUtils.getNumericAttributes( x1 );
		keys.addAll( LearnerUtils.getNumericAttributes( x2 ) );
		Double sum = 0.0d;

		for( String key : keys ){

			if( ! DataUtils.isAnnotation( key ) ){
				Double d1 = LearnerUtils.getDouble( key, x1 );
				if( d1 == null || Double.isNaN( d1 ) )
					d1 = 0.0d;

				Double d2 = LearnerUtils.getDouble( key, x2 );
				if( d2 == null || Double.isNaN( d2 ) )
					d2 = 0.0d;

				//log.info( "  x[{}]  -  y[{}]  = " + (d1 - d2), key, key );
				sum += ((d1 - d2) * (d1 - d2));
			}
		}

		return Math.sqrt( sum );
	}
}