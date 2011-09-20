package stream.data.vector;

import java.util.HashMap;

public class InputVector extends Vector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1868992010251953558L;

	double y;
	
	public void setLabel(double y) {
		this.y = y;
	}
	
	public double getLabel() {
		return y;
	}
	
	public InputVector() {
		super();
	}
		
	/**
	 * Creates a dense vector from the given values
	 * 
	 * @param vals
	 * @param copy
	 * @param y
	 */
	public InputVector(double[] vals, boolean copy, double y) {
		super(vals, copy);
		this.y = y;
	}
	
	/**
	 * Creates a sparse vector from the given values
	 * 
	 * @param pairs
	 * @param copy
	 * @param y
	 */
	public InputVector(HashMap<Integer,Double> pairs, boolean copy, double y) {
		super(pairs, copy);
		this.y = y;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param x
	 */
	public InputVector(InputVector x) {
		super(x);
	}
}
