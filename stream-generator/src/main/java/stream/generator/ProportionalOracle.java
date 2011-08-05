/**
 * 
 */
package stream.generator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a proportional oracle, which will predict values from a fixed,
 * nominal set, where each nominal value has a specific weight. The values are
 * drawn from a unified random distribution, weighted by a predefined weighting
 * scheme.
 * </p>
 * <p>
 * Thus, it allows for pre-defining the proportion of each of the nominal values
 * in the final outcome.
 * </p>
 * 
 * @author chris
 *
 */
public class ProportionalOracle {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( ProportionalOracle.class );
	
	/* The random generator of this oracle */
	Random rnd;
	
	/* The interval bounds for each class/nominal value */
	TreeMap<Double,String> map = new TreeMap<Double,String>();
	
	Map<String,Double> weights = new LinkedHashMap<String,Double>();
	
	public ProportionalOracle(){
		this( System.currentTimeMillis() );
	}
	
	public ProportionalOracle( Long seed ){
		setSeed( seed );
	}
	
	
	public void setSeed( Long seed ){
		rnd = new Random( seed );
	}
	
	
	public void setWeights( Map<String,Double> weights ){
		
		Double total = 0.0d;
		for( Double d : weights.values() )
			total += d;
		
		Double offset = 0.0d;
		
		for( String key : weights.keySet() ){
			Double w = weights.get( key ) / total;
			map.put( offset + w, key );
			offset += w;
			this.weights.put( key, w );
		}
	}
	
	public Set<String> getLabels(){
		return weights.keySet();
	}
	
	public Double getWeight( String key ){
		return weights.get( key );
	}
	
	public String getNext(){
		Double d = rnd.nextDouble();
		Double last = 0.0d;
		for( Double k : map.keySet() ){
			if( last < d && d <= k ){
				String found = map.get( k );
				log.debug( "Returning value: {}", found );
				return found;
			}
			last = k;
		}
		
		String value = map.lastEntry().getValue();
		log.debug( "Random index was {}, returning default: {}", d, value );
		return value;
	}
	
	
	public String toString(){
		StringBuffer s = new StringBuffer();
		
		Double last = 0.0d;
		for( Double d : map.keySet() ){
			s.append( "   [" + last + " : " + d + " ]  => " + map.get( d ) + "\n");
			last = d;
		}
		
		return s.toString();
	}
	
	
	public static void main( String[] args ){
		ProportionalOracle o = new ProportionalOracle();
		Map<String,Double> weights = new HashMap<String,Double>();
		weights.put( "class0", 25.0 );
		weights.put( "class1", 70.0 );
		weights.put( "class2",  5.0 );
		
		o.setWeights( weights );
		
		Map<String,Double> counts = new LinkedHashMap<String,Double>();
		Double total = 0.0d;
		for( int i = 0; i < 100000; i++ ){
			String next = o.getNext();
			Double count = counts.get( next );
			if( count == null )
				count = 1.0d;
			else
				count = count + 1.0d;
			counts.put( next, count );
			log.debug( "next: {}", o.getNext() );
			total += 1.0d;
		}
		
		for( String key : counts.keySet() ){
			log.info( "   count( {} ) = {}", key, counts.get( key ) );
			log.info( "      weight: {}", counts.get( key ) / total );
		}
	}
}