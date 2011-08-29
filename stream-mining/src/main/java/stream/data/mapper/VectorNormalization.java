package stream.data.mapper;

import java.util.Set;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.learner.LearnerUtils;

public class VectorNormalization implements DataProcessor {

	@Override
	public Data process(Data data) {
		
		Set<String> keys = LearnerUtils.getNumericAttributes( data );
		Double sum = 0.0d;
		for( String key : keys ){
			Double d = LearnerUtils.getDouble( key, data );
			if( ! Double.isNaN( d ) )
				sum += d;
		}
		if( sum == 0.0d )
			return data;
		
		for( String key : keys ){
			Double d = LearnerUtils.getDouble( key, data );
			if( ! Double.isNaN( d ) )
				data.put( key, d / sum );
		}
		
		return data;
	}
}