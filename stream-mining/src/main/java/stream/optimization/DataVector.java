package stream.optimization;

import java.util.Set;

import stream.data.DataImpl;

public class DataVector extends DataImpl implements Vector {

	/** The unique class ID */
	private static final long serialVersionUID = 1764393665317323676L;
	Double scale = 1.0d;
	
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

	@Override
	public Vector add(Vector vec) {
		if( vec instanceof DataVector ){
			
			Set<String> keys = keySet();
			DataVector v = (DataVector) vec;
			if( v.keySet().size() < keys.size() )
				keys = v.keySet();
			
			for( String key : keys ){
				if( containsKey( key ) ){
					Double x1 = (scale) *  ( (Double) get( key ) );
					Double x2 = (v.scale) *  (Double) v.get( key );
					put( key, x1 + x2 );
				}
			}
		}
		return this;
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
				if( containsKey( key ) ){
					Double x1 = (scale) *   ( (Double) get( key ) );
					Double x2 = (v.scale) * (Double) v.get( key );
					sum += (x1 * x2);
				}
			}
			
			return sum;
		}

		return 0;
	}
}