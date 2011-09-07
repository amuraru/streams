package stream.optimization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.learner.Learner;
import stream.model.PredictionModel;

public class StochasticGradientDescent 
	implements Learner<Vector,PredictionModel<Vector,Double>>, PredictionModel<Vector,Double>
{
	/** The unique class ID */
	private static final long serialVersionUID = 2773277678142526444L;
	
	static Logger log = LoggerFactory.getLogger( StochasticGradientDescent.class );

	double t = 0.0;
	double b;
	Vector gt;
	Vector w;
	Vector avg_w;
	double sum_etha;
	
	double D;
	ObjectiveFunction objFunction;
	
	
	public StochasticGradientDescent( ObjectiveFunction of ){
		this.objFunction = of;
	}
	
	
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
		t = 0.0d;
		b = 0.0d;
		sum_etha = 0.0d;
		w = new DataVector();
		avg_w = new DataVector();
	}
	
	
	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn( Vector x_i ) {
		t = t + 1.0d;
		double label = x_i.getLabel();
		gt = objFunction.subgradient( w, x_i, label );
		
		w = w.sparsify();
		
		double eta = etha();
		Vector gt_scaled = gt.scale( (-1.0d) * eta );
		w.add( gt_scaled );
		
		double n = w.norm();
		if( n > D ){
			w = w.scale( D / n );
		}
		
		double sc1 = sum_etha / (sum_etha + etha() );
		double sc2 = etha() / (sum_etha + etha() );
		
		avg_w.scale( sc1 ).add( w.scale( sc2 ) );
		sum_etha += etha();
		log.info( "w.size() = {}", w.size() );
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
	
	public void printModel(){
		log.info( "snorm = {}", w.snorm() );
		//log.info( "b = {}", b );
		//log.info( "w_t = {}", w );
	}
}