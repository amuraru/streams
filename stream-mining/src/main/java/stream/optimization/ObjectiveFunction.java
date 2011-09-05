package stream.optimization;


public interface ObjectiveFunction {

	/**
	 * Implements function application of the specific objective function.
	 * 
	 * @param item
	 * @return
	 */
	public Double apply( Vector item );
	
	
	/**
	 * Computes the subgradient of the objective function for the specified
	 * vector.
	 * 
	 * @param item
	 * @return
	 */
	public Vector subgradient( Vector item );
}