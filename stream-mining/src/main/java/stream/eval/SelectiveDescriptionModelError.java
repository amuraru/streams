package stream.eval;
/**
 * 
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import stream.data.stats.Statistics;
import stream.model.SelectiveDescriptionModel;

/**
 * @author chris
 *
 */
public class SelectiveDescriptionModelError<T> {

	Collection<T> probes;

	
	public SelectiveDescriptionModelError( Collection<T> probes ){
		this.probes = probes;
	}
	
	
	public SelectiveDescriptionModelError( T[] probes ){
		this.probes = new ArrayList<T>();
		for( T t : probes )
			this.probes.add( t );
	}
	
	
	public Statistics computeError( SelectiveDescriptionModel<T,Double> truth, Map<String,SelectiveDescriptionModel<T,Double>> models ){
		Statistics s = new Statistics();
		for( String key : models.keySet() ){
			Double err = computeError( truth, models.get( key ) );
			s.put( key, err );
		}
		return s;
	}
	
	
	public Double computeError( SelectiveDescriptionModel<T,Double> truth, SelectiveDescriptionModel<T,Double> model ){
		Double error = 0.0d;
		
		Iterator<T> it = probes.iterator();
		while(it.hasNext() ){
			T probe = it.next();
			Double trueValue = truth.describe( probe );
			Double predValue = model.describe( probe );
			error += Math.abs(predValue - trueValue);
		}

		return error;
	}
}