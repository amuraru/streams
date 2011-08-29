package stream.quantiles;


/**
 * 
 * @author Markus Kokott, Carsten Przyluczky
 *
 */

public abstract class ProbabilisticQuantileEstimator implements QuantileLearner{
	private static final long serialVersionUID = 8262779722828711062L;
	protected float epsilon;
	protected float delta;
	
	public ProbabilisticQuantileEstimator(float epsilon, float delta){
		this.delta = delta;
		this.epsilon = epsilon;
	}

	public void init(){
	}
}
