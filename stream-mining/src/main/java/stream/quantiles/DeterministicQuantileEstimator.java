package stream.quantiles;


/**
 * Any deterministic quantile estimator should extend this class. It implements {@link QuantileEstimator}
 * and provides an appropriate constructor for general deterministic quantile estimators.
 * 
 * @author Markus Kokott, Carsten Przyluczky
 * @see QuantileEstimator
 */

public abstract class DeterministicQuantileEstimator implements QuantileLearner {
	private static final long serialVersionUID = 5886919177952931056L;
	/**
	 * This value specifies the error bound.
	 */
	protected double epsilon;
	
	/**
	 * This constructor spawns a deterministic quantile estimator with specified error bound.
	 * @param epsilon - an error parameter. <code>float</code> values between 0 and 1 are allowed.
	 * @throws RuntimeException if epsilon doesn't fit into an error parameters codomain 
	 */
	public DeterministicQuantileEstimator (double epsilon){
		
		if (epsilon <= 0 || epsilon >= 1){
			throw new RuntimeException("An appropriate epsilon value must lay between 0 and 1.");
		}
		
		this.epsilon = epsilon;
	}

	/**
	 * Returns the error parameter.
	 * @return <code>float</code> value of error parameter epsilon
	 */
	public double getEpsilon() {
		return epsilon;
	}
	
	public void init(){
	}
}
