/**
 * 
 */
package stream.data.stats;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class Statistics extends LinkedHashMap<String, Double> {

	/** The unique class ID*/
	private static final long serialVersionUID = 5994452860264445162L;
	
	String name = "";
	String key = "";
	
	public String getName(){
		return this.name;
	}
	
	public Statistics setName( String name ){
		this.name = name;
		return this;
	}
	
	
	
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public Statistics setKey(String key) {
		this.key = key;
		return this;
	}

	/**
	 * Adds the given statistics vector to this instance.
	 * 
	 * @param st
	 */
	public synchronized void add( Map<String,Double> st ){
		for( String key : st.keySet() ){
			Double d = get( key );
			if( d == null )
				d = st.get( key );
			else
				d += st.get( key );
			
			put( key, d );
		}
	}
	
	
	public synchronized void substract( Map<String,Double> st ){
		for( String key : st.keySet() ){
			Double d = get( key );
			if( d == null )
				d = -st.get( key );
			else
				d -= st.get( key );
			
			put( key, d );
		}
	}
	
	
	public synchronized void max( Map<String,Double> st ){
		
		List<String> keys = new LinkedList<String>( this.keySet() );
		for( String k : st.keySet() )
			if( ! keys.contains( k ) )
				keys.add( k );
		
		for( String key : keys )
			this.put( key, max( get( key ), st.get( key ) ) );
	}
	
	private Double max( Double d1, Double d2 ){
		if( d1 == null && d2 == null )
			return Double.NaN;
		
		if( d1 == null )
			return d2;
		
		if( d2 == null )
			return d1;
		
		return Math.max( d1, d2 );
	}
	
	public Statistics divideBy( String key ){
		Statistics st = new Statistics();
		st.putAll( this );
		
		Double x = get( key );
		if( x == null || x == 0.0d )
			return st;
		
		for( String k : keySet() ){
			if( ! k.equals( key ) ){
				Double val = get( k );
				st.put( k, val / x );
			}
		}
		return st;
	}
	
	
	public Statistics divideBy( Double val ){
		Statistics st = new Statistics();
		for( String k : keySet() )
			st.put( k, get(k) / val );
		
		return st;
	}
	
	
	public void add( String key, Double val ){
		if( this.containsKey( key ) ){
			Double d = get( key ) + val;
			put( key, d );
		} else
			put( key, val );
	}
}