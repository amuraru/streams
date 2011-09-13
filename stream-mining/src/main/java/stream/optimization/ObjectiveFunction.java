package stream.optimization;

import stream.data.vector.SparseVector;


public interface ObjectiveFunction {

	/**
	 * Implements function application of the specific objective function.
	 * 
	 * @param item
	 * @return
	 */
	public Double apply( SparseVector item );
	
	
	/**
	 * Computes the subgradient of the objective function for the specified
	 * vector.
	 * 
	 * @param item
	 * @return
	 */
	public SparseVector subgradient( SparseVector w_t, SparseVector x_i, Double y_i );
}