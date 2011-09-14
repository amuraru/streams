package stream.optimization;

import stream.data.vector.Vector;
import stream.data.vector.InputVector;


public interface ObjectiveFunction {

	/**
	 * Implements function application of the specific objective function.
	 * 
	 * @param item
	 * @return
	 */
	public double apply( Vector w );
	
	
	/**
	 * Computes the subgradient of the objective function for the specified
	 * vector.
	 * 
	 * @param item
	 * @return
	 */
	public Vector subgradient( Vector w, InputVector x_i, double y_i );
}