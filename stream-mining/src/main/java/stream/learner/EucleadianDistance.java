package stream.learner;

import java.util.Set;

import stream.data.Data;
import stream.data.DataUtils;

public class EucleadianDistance implements Distance {

	@Override
	public Double distance(Data x1, Data x2) {

		Set<String> keys = LearnerUtils.getNumericAttributes( x1 );
		keys.addAll( LearnerUtils.getNumericAttributes( x2 ) );
		Double sum = 0.0d;

		for( String key : keys ){

			if( ! DataUtils.isAnnotation( key ) ){
				Double d1 = LearnerUtils.getDouble( key, x1 );
				if( d1 == null )
					d1 = 0.0d;

				Double d2 = LearnerUtils.getDouble( key, x2 );
				if( d2 == null )
					d2 = 0.0d;

				sum += ((d1 - d2) * (d1 - d2));
			}
		}

		return Math.sqrt( sum );
	}
}