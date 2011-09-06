package stream.optimization;

import java.util.HashSet;
import java.util.Set;

import stream.data.Data;
import stream.data.DataImpl;

public class DataVector extends DataImpl implements Vector {

	/** The unique class ID */
	private static final long serialVersionUID = 1764393665317323676L;
	Double scale = 1.0d;
	
	
	public DataVector(){
	}
	
	public DataVector( Data data ){
		super( data );
	}
	
	
	@Override
	public void set(int i, double d) {
		put( i + "", new Double( d ) );
	}

	@Override
	public double get(int i) {
		return scale * ( (Double) get( i + "" ) );
	}

	@Override
	public Vector scale(double d) {
		scale = scale * d;
		return this;
	}

	
	public void add( double factor, Vector vec ){
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
	
	@Override
	public void add(Vector vec) {
		add( 1.0d, vec );
	}

	public double snorm() {
		return innerProduct( this );
	}
	
	@Override
	public double norm(){
		return Math.sqrt( innerProduct( this ) );
	}

	@Override
	public double innerProduct(Vector vec) {
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

	@Override
	public double getLabel() {
		return (Double) get( "@label" );
	}

	@Override
	public Vector sparsify() {
		
		Set<String> zeroKeys = new HashSet<String>();
		for( String key : keySet() ){
			if( ( (Double) get( key ) ).doubleValue() == 0.0d ){
				zeroKeys.add( key );
			}
		}
		
		for( String key : zeroKeys ){
			remove( key );
		}

		return this;
	}
}