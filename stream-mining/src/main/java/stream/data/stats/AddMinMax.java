/**
 * 
 */
package stream.data.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import stream.data.mapper.Mapper;

/**
 * @author chris
 *
 */
public class AddMinMax implements
		Mapper<Map<String, Object>, Map<String,Object>> {

	Set<String> features;
	
	Map<String,Double> maxima = new HashMap<String,Double>();
	Map<String,Double> minima = new HashMap<String,Double>();
	
	
	public AddMinMax(){
		
	}
	
	
	/* (non-Javadoc)
	 * @see stream.data.mapper.DataMapper#map(java.lang.Object)
	 */
	@Override
	public Map<String, Object> map(Map<String, Object> input) throws Exception {
		
		detectNumericFeatures( input );
		
		for( String f : features ){
			if( input.containsKey( f ) ){
				try {
					Double val = (Double) input.get( f );
					Double max = maxima.get( f );
					if( max == null )
						max = val;
					else
						max = Math.max( max, val );
					maxima.put( f, max );
					
					Double min = minima.get( f );
					if( min == null )
						min = val;
					else
						min = Math.min( min, val );
					minima.put( f, min );
					input.put( getMaxName(f), max );
					input.put( getMinName(f), min );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return input;
	}
	
	
	public void detectNumericFeatures( Map<String,Object> datum ){
		if( features == null )
			features = new HashSet<String>();
		
		for( String key : datum.keySet() ){
			if( ! features.contains( key ) && datum.get(key).equals( Double.class ) ){
				features.add( key );
			}
		}
	}
	
	public String getMaxName( String name ){
		return "@max:" + name;
	}
	
	public String getMinName( String name ){
		return "@min" + name;
	}
}