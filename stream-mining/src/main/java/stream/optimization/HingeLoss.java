package stream.optimization;

public class HingeLoss implements ObjectiveFunction {

	double lambda = 1.0d;
	
	public HingeLoss(){
	}
	
	
	@Override
	public Double apply(Vector item) {
		return 0.0d;
	}

	@Override
	public Vector subgradient(Vector w_t, Vector x_i, Double label ) {
		double d = label * w_t.innerProduct( x_i );
		if( d < 1 ){
			
			Vector x_i_scaled = x_i.scale( -1.0d * label );
			x_i_scaled.add( lambda, w_t ); // w_t_scaled.add( x_i_scaled );
			
			return x_i_scaled; //w_t.scale( lambda ).add( x_i.scale( -1.0d * label ) );
		} else {
			return w_t.scale( lambda );
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