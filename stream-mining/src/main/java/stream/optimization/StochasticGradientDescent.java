package stream.optimization;

import stream.learner.Learner;
import stream.model.PredictionModel;

public class StochasticGradientDescent 
	implements Learner<Vector,PredictionModel<Vector,Double>>, PredictionModel<Vector,Double>
{
	/** The unique class ID */
	private static final long serialVersionUID = 2773277678142526444L;

	double t;
	double b;
	Vector gt;
	Vector w;
	Vector avg_w;
	double sum_etha;
	
	double D;
	ObjectiveFunction objFunction;
	
	
	protected double etha(){
		return 1.0d / t;
	}
	
	public double getD() {
		return D;
	}

	public void setD(double d) {
		D = d;
	}


	@Override
	public void init() {
		gt = new DataVector();
		t = 1.0d;
		b = 0.0d;
		sum_etha = 0.0d;
		w = new DataVector();
		avg_w = new DataVector();
	}
	
	
	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn( Vector w_t ) {

		gt = objFunction.subgradient( w_t );
		
		w = w.add( gt.scale( (-1) * etha() ) );
		
		double n = w.norm();
		if( n > D ){
			w = w.scale( D / n );
		}
		
		double sc1 = sum_etha / (sum_etha + etha() );
		double sc2 = etha() / (sum_etha + etha() );
		
		avg_w = avg_w.scale( sc1 ).add( w.scale( sc2 ) );
		sum_etha += etha();
	}
	
	
	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public Double predict(Vector item) {
		if( ( b + w.innerProduct( item ) ) < 0.0 )
			return -1.0d;
		else
			return 1.0d;
	}

	
	/**
	 * @see stream.learner.Learner#getModel()
	 */
	@Override
	public PredictionModel<Vector, Double> getModel() {
		return this;
	}
}