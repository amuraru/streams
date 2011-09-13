package stream.optimization;

import stream.data.vector.SparseVector;

public class SvmHingeLoss implements SgdObjectiveFunction {

	double lambda = 1.0e-6;
	double radius;							// radius for all components
	double grad_variance;
	int	samples = 0;
	boolean use_b = true;
	double radius_b = 1000;					// some large value
	SparseVector w_t;
	
	public SvmHingeLoss(){
	}
	
	@Override
	public void useIntercept(boolean useb) {
		this.use_b = useb;
	}
	
	@Override
	public void setLambda(double lambda) {
		this.lambda = lambda;
		if (use_b)
			setRadius(Math.sqrt(1./lambda + radius_b*radius_b));
		else
			setRadius(Math.sqrt(1./lambda));
	}

	@Override
	public double getLambda() {
		return lambda;
	}

	@Override
	public void setRadius(double radius) {
		this.radius = radius;		
	}

	@Override
	public double getRadius() {
		return radius;
	}

	@Override
	public void setGradientNormVariance(double variance) {
		this.grad_variance = variance;
	}

	@Override
	public double getGradientNormVariance() {
		return grad_variance;
	}
	
	/*
	 * Estimate G using training examples, using the zero vector as the iterate.
	 * 
	 */
	public double estimateGradientVariance (SparseVector x_i) {
		if (use_b)
			grad_variance = (samples*grad_variance + x_i.snorm() + 1.) / (samples + 1.); 	// TODO use this if b is present
		else
			grad_variance = (samples*grad_variance + x_i.snorm()) / (samples + 1.);
		++samples;
		return grad_variance;
	}
	
	
	@Override
	public Double apply( SparseVector item ) {
		return 0.0d;
	}

	@Override
	public SparseVector subgradient( SparseVector w, SparseVector x_i, Double label ) {
		
		w_t = w;
		double d = label * w_t.innerProduct( x_i );

		w_t.scale(lambda);
		if( d < 1 ){
			w_t.add( -1.0d*label, x_i );
		}
		return w_t;
	}


}