package stream.optimization;

import stream.data.vector.SparseVector;

public interface SgdObjectiveFunction extends ObjectiveFunction {
	
	public void useIntercept( boolean useb );
	
	public void setLambda( double lambda );
	
	public double getLambda();
	
	public void setRadius( double radius );
	
	public double getRadius();
	
	public void setGradientNormVariance ( double variance );
	
	public double getGradientNormVariance ();
	
	public double estimateGradientVariance (SparseVector x_i);
	
}
