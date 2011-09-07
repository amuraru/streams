package stream.optimization;

import stream.data.vector.SparseVector;

public class HingeLoss implements ObjectiveFunction {

	double lambda = 1.0e-6;
	
	public HingeLoss(){
	}
	
	
	@Override
	public Double apply( SparseVector item) {
		return 0.0d;
	}

	@Override
	public SparseVector subgradient( SparseVector w, SparseVector x_i, Double label ) {
		
		SparseVector w_t = new SparseVector( w );
		
		double d = label * w_t.innerProduct( x_i );
		if( d < 1 ){
			x_i.scale( -1.0d * label );
			x_i.add( lambda, w_t );
			return x_i;
		} else {
			w_t.scale( lambda );
			return w_t;
		}
	}


	@Override
	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	@Override
	public double getLambda() {
		return lambda;
	}
}