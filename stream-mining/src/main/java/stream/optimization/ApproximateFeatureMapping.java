package stream.optimization;

import stream.data.vector.SparseVector;

public interface ApproximateFeatureMapping {

	public void setDimension( int approx_dimension );
	
	public int getDimension();
	
	/**
	 * Prepare random vectors
	 * 
	 * @param
	 * @return
	 */
	public void init();
	
	/**
	 * Transforms an input vector to an n-dimensional feature vector, according to the kernel type and dimension specified.
	 * 
	 * @param input
	 * @return
	 */
	public SparseVector transform( SparseVector input );
	
}
