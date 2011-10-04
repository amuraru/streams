package stream.optimization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.Measurable;
import stream.data.vector.InputVector;
import stream.data.vector.Vector;
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
	double avg_b;
	boolean useb = true;
	Vector gt;
	Vector w;
	Vector avg_w;
	double sum_etha;
	
	GaussianFeatureMapping gaussianKernel;
	boolean useKernel = false;
	int dimension = -1;
	
	int gradVarMaxSamples = 100;				// Number of samples to be used for estimating gradient norm variance
	int gradVarSamples = 1;
	SgdObjectiveFunction obj;
	
	public StochasticGradientDescent(){
		this( new SvmHingeLoss() );
	}
	
	public StochasticGradientDescent( SgdObjectiveFunction obj) {
		this( obj, true );
	}
	
	public StochasticGradientDescent( SgdObjectiveFunction obj, boolean useb ){
		this.obj = obj;
		this.useb = useb;
	}
	
	public void useGaussianKernel( double gamma, int dimension, boolean use_gpu ) {
		this.dimension = dimension;
		gaussianKernel = new GaussianFeatureMapping( gamma, dimension, use_gpu );
		useKernel = true;
	}

	public Vector getWeightVector(){
		return avg_w;
	}
	
	public void setWeightVector( Vector v ){
		this.avg_w = v;
	}
	
	public Double getIntercept(){
		return this.avg_b;
	}
	
	public void setIntercept( Double d ){
		this.avg_b = d;
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
		//return 1.0d / t;
		return obj.getRadius() / Math.sqrt( obj.getGradientNormVariance() * t);
		//return obj.getRadius() / Math.sqrt( obj.getGradientNormVariance() * 1000.0d );
		//return 1.0d / (obj.getLambda() * t);
	}
	
	@Override
	public void init() {
		t = 0.0d;
		b = 0.0d;
		avg_b = 0.0d;
		sum_etha = 0.0d;
		if(!this.useKernel) { // use sparse representation
			gt = new Vector();
			w = new Vector();
			avg_w = new Vector();
		} else {				// use dense representation
			gt = new Vector(dimension);
			w = new Vector(dimension);
			avg_w = new Vector(dimension);
		}
	}
	
	
	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn( Data example ) {
		InputVector x_i;
		
		if( w == null )
			init();
		
		InputVector input_i = Vector.createSparseVector( example );
		if( input_i == null ){
			log.error( "Cannot create sparse-vector from example: {}", example );
			log.error( "Will not use this data point for training!" );
			return;
		}
		
		if(useKernel)
			x_i = gaussianKernel.transform(input_i);
		else
			x_i = input_i;

		if( this.gradVarSamples < this.gradVarMaxSamples ) {
			obj.estimateGradientVariance(x_i);
			++this.gradVarSamples;
			//System.out.println("SGD Init: obj.G = " + Math.sqrt(obj.getGradientNormVariance()));
			return;
		}
		
		t = t + 1.0d;
		double label = x_i.getLabel();
		double eta = etha();

		/*
		gt = obj.subgradient( w, x_i, label );
		// w = w + (-1.0 * eta) gt
		//
		w = w.add( (-1.0 * eta), gt );
		*/
		
		double prediction = label*(w.innerProduct(x_i) + b);
		w.scale(1.0 - obj.getLambda()*eta);
		if(prediction < 1) {
			w.add( label*eta, x_i );
			b = b + label*eta;
		}
		
		double norm;
		if(useb)
			norm = Math.sqrt( w.snorm() + b*b );
		else
			norm = w.norm();
		
		if( norm > obj.getRadius() ){
			double scalar = obj.getRadius() / norm;
			w.scale( scalar );
			if(useb)
				b *= scalar;
		}
		
		double sc1 = sum_etha / (sum_etha + etha() );
		double sc2 = etha() / (sum_etha + etha() );

		avg_w.scale( sc1 );
		avg_w.add( sc2, w );
		avg_b = avg_b*sc1 + sc2*b;
		sum_etha += etha();
	}
	
	
	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public Double predict(Data example) {
		InputVector x_i;
		InputVector item = Vector.createSparseVector( example );
		if(useKernel) 
			x_i = gaussianKernel.transform(item);
		else
			x_i = item;
		
		if( ( avg_w.innerProduct( x_i ) + b) < 0.0 )
			return -1.0d;
		else
			return 1.0d;
	}
	
	public Double predict( Vector weightVector, Data example ){
		InputVector x_i;
		InputVector item = Vector.createSparseVector( example );
		if(useKernel) 
			x_i = gaussianKernel.transform(item);
		else
			x_i = item;
		
		if( ( weightVector.innerProduct( x_i ) + b) < 0.0 )
			return -1.0d;
		else
			return 1.0d;
	}
	
	
	public void printModel(){
		log.info( "snorm = {}", avg_w.snorm() );
		log.info( "  avg_w.size() is {}", avg_w.length() );
		log.info( "  b = {}", b );
		//log.info( "w_t = {}", w );
	}
}