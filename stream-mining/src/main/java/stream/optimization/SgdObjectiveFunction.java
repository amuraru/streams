package stream.optimization;

import stream.data.vector.InputVector;

public interface SgdObjectiveFunction extends ObjectiveFunction {
	
	public void useIntercept( boolean useb );
	
	public void setLambda( double lambda );
	
	public double getLambda();
	
	public void setRadius( double radius );
	
	public double getRadius();
	
	public void setGradientNormVariance ( double variance );
	
	public double getGradientNormVariance ();
	
	public double estimateGradientVariance (InputVector x_i);
		
}
