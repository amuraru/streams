package stream.optimization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.Measurable;
import stream.data.vector.SparseVector;
import stream.learner.AbstractClassifier;

public class StochasticGradientDescent 
	extends AbstractClassifier<Data,Double>
	implements Measurable
//	implements Learner<Data,PredictionModel<Data,Double>>, PredictionModel<Data,Double>, Measurable
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
	
	
	public StochasticGradientDescent(){
		this( new HingeLoss() );
	}
	
	public StochasticGradientDescent( ObjectiveFunction of ){
		this.objFunction = of;
	}
	


	@Override
	public double getByteSize() {
		//
		// 32 bytes is from t, b, sum_etha and D as each is stored as 64bit double
		//
		double bs = 32.0d;
		if( gt != null )
			bs += gt.getByteSize();
		
		if( w != null )
			bs += w.getByteSize();
		
		if( avg_w != null )
			bs += avg_w.getByteSize();
		
		return bs;
	}

	
	
	protected double etha(){
		return 1.0d / t;
	}
	
	public Double getD() {
		return D;
	}

	public void setD( Double d ) {
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
		
		if( w == null )
			init();
		
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
		if( n > getD() ){
			w.scale( getD() / n );
		}
		
		double sc1 = sum_etha / (sum_etha + etha() );
		double sc2 = etha() / (sum_etha + etha() );

		w.scale( sc2 );
		avg_w.scale( sc1 );

		avg_w = avg_w.add( 1.0d, w );
		sum_etha += etha();
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
	
	public void printModel(){
		log.info( "snorm = {}", w.snorm() );
		log.info( "  w.size() is {}", w.size() );
		//log.info( "b = {}", b );
		//log.info( "w_t = {}", w );
	}
}