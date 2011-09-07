package stream.optimization;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.vector.SparseVector;
import stream.learner.Learner;
import stream.model.PredictionModel;

public class StochasticGradientDescent 
	implements Learner<Data,PredictionModel<Data,Double>>, PredictionModel<Data,Double>
{
	/** The unique class ID */
	private static final long serialVersionUID = 2773277678142526444L;
	
	static Logger log = LoggerFactory.getLogger( StochasticGradientDescent.class );

	double t = 0.0;
	double b;
	SparseVector gt;
	SparseVector w;
	SparseVector avg_w;
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
		gt = new SparseVector();
		t = 0.0d;
		b = 0.0d;
		sum_etha = 0.0d;
		w = new SparseVector();
		avg_w = new SparseVector();
	}
	
	
	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn( Data example ) {
		
		SparseVector x_i = this.createSparseVector( example );
		if( x_i == null ){
			log.error( "Cannot create sparse-vector from example: {}", example );
			log.error( "Will not use this data point for training!" );
			return;
		}
		
		t = t + 1.0d;
		double label = x_i.getLabel();
		gt = objFunction.subgradient( w, x_i, label );
		
		double eta = etha();
		
		// w = w + (-1.0 * eta) gt
		//
		w = w.add( (-1.0 * eta), gt );
		
		double n = w.norm();
		if( n > D ){
			w.scale( D / n );
		}
		
		double sc1 = sum_etha / (sum_etha + etha() );
		double sc2 = etha() / (sum_etha + etha() );

		w.scale( sc2 );
		avg_w.scale( sc1 );

		avg_w = avg_w.add( 1.0d, w );
		sum_etha += etha();
		log.info( "w.size() = {}", w.size() );
	}
	
	
	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public Double predict(Data example) {
		SparseVector item = this.createSparseVector( example );
		
		if( ( b + w.innerProduct( item ) ) < 0.0 )
			return -1.0d;
		else
			return 1.0d;
	}

	
	/**
	 * @see stream.learner.Learner#getModel()
	 */
	@Override
	public PredictionModel<Data, Double> getModel() {
		return this;
	}
	
	public void printModel(){
		log.info( "snorm = {}", w.snorm() );
		log.info( "  w.size() is {}", w.size() );
		//log.info( "b = {}", b );
		//log.info( "w_t = {}", w );
	}
	
	public SparseVector createSparseVector( Data datum ){
		if( datum.containsKey( ".sparse-vector" ) )
			return (SparseVector) datum.get( ".sparse-vector" );
		
		for( Serializable val : datum.values() ){
			if( val instanceof SparseVector ){
				return (SparseVector) val;
			}
		}
		
		return null;
	}
}