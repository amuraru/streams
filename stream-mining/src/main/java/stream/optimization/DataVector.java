package stream.optimization;

import java.util.HashSet;
import java.util.Set;

import stream.data.Data;
import stream.data.DataImpl;

public class DataVector extends DataImpl {

	/** The unique class ID */
	private static final long serialVersionUID = 1764393665317323676L;
	Double scale = 1.0d;
	int maxIndex = -1;
	
	public DataVector(){
	}
	
	public DataVector( Data data ){
		super( data );
	}
	
	
	public void set(int i, double d) {
		put( i + "", new Double( d ) );
	}

	public double get(int i) {
		
		String key = "" + i;
		Double val = (Double) get( key );
		if( val == null )
			return 0.0d;
		
		return scale * val;
	}

	public DataVector scale(double d) {
		scale = scale * d;
		return this;
	}

	
	public void add( double factor, DataVector vec ){
		if( vec instanceof DataVector ){
			
			DataVector v = (DataVector) vec;
			Set<String> keys = new HashSet<String>( keySet() );
			keys.addAll( v.keySet() );
			
			for( String key : keys ){
				Double x1 = 0.0d;
				if( containsKey( key ) ){
					x1 = (scale) *  ( (Double) get( key ) );
				}
				
				Double x2 = 0.0d;
				if( v.containsKey( key ) )
					x2 = (v.scale) *  (Double) v.get( key );
				
				put( key, x1 + factor * x2 );
			}
		}
	}
	
	public void add(DataVector vec) {
		add( 1.0d, vec );
	}

	public double snorm() {
		return innerProduct( this );
	}
	
	public double norm(){
		return Math.sqrt( innerProduct( this ) );
	}

	public double innerProduct(DataVector vec) {
		if( vec instanceof DataVector ){
			
			Set<String> keys = keySet();
			DataVector v = (DataVector) vec;
			if( v.keySet().size() < keys.size() )
				keys = v.keySet();

			Double sum = 0.0d;
			
			for( String key : keys ){
				Double x1 = 0.0d;
				Double x2 = 0.0d;
				
				if( containsKey( key ) ){
					x1 = (scale) *   ( (Double) get( key ) );
				}
				
				if( v.containsKey( key ) ){
					x2 = (v.scale) * (Double) v.get( key );
				}
				
				sum += (x1 * x2);
			}
			
			return sum;
		}

		return Double.NaN;
	}

	public double getLabel() {
		return (Double) get( "@label" );
	}

	public void sparsify() {
		
		int max = 0;
		
		Set<String> zeroKeys = new HashSet<String>();
		for( String key : keySet() ){
			if( ( (Double) get( key ) ).doubleValue() == 0.0d ){
				zeroKeys.add( key );
			} else {
				try {
					Integer idx = new Integer( key );
					if( max < idx )
						max = idx;
				} catch (Exception e) {
				}
			}
		}
		
		for( String key : zeroKeys ){
			remove( key );
		}

		//return this;
	}

	public double getByteSize() {
		return Double.NaN;
	}

	public int getNumberOfNonZeros() {
		return this.size();
	}

	public int getMaxIndex() {
		return maxIndex;
	}
}