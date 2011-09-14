package stream.learner;

import java.io.Serializable;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.Measurable;
import stream.data.vector.Vector;
import stream.data.vector.InputVector;
import edu.udo.cs.pg542.util.Kernel;

/**
 * Perceptron stream learner
 * 
 * @author Helge Homburg, Christian Bockermann
 */
public class Perceptron 
	extends AbstractClassifier<Data,Double>
	implements Measurable
{
	private static final long serialVersionUID = -3263838547557335984L;

	static Logger log = LoggerFactory.getLogger( Perceptron.class );

	/* The learning rate gamma */
	Double learnRate = 1.0d;

	/* The label attribute */
	String labelAttribute;

	/* The default labels predicted by this model */
	LinkedHashSet<Double> labels = new LinkedHashSet<Double>();

	double beta0 = 0.0d;
	Vector beta = new Vector();

	
	public Perceptron() {
		this(Kernel.INNER_PRODUCT, 0.05);
	}

	public Perceptron(int learnRate) {
		this(Kernel.INNER_PRODUCT, learnRate);
	}

	public Perceptron(int kernelType, double learnRate) {       
		//this.model = new HyperplaneModel(kernelType);
		//this.model.initModel( new LinkedHashMap<String,Double>(), 0.0d );
		this.learnRate = learnRate;
	}

	
	
	
	/**
	 * @return the labelAttribute
	 */
	public String getLabelAttribute() {
		return labelAttribute;
	}

	/**
	 * @param labelAttribute the labelAttribute to set
	 */
	public void setLabelAttribute(String labelAttribute) {
		this.labelAttribute = labelAttribute;
	}

	/**
	 * @return the learnRate
	 */
	public Double getLearnRate() {
		return learnRate;
	}

	/**
	 * @param learnRate the learnRate to set
	 */
	public void setLearnRate(Double learnRate) {
		this.learnRate = learnRate;
	}


	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn(Data item) {

		if( labelAttribute == null )
			labelAttribute = LearnerUtils.detectLabelAttribute( item );


		if( labelAttribute == null ){
			log.info( "No label defined!" );
			return;
		}

		Serializable val = item.get( labelAttribute );
		if( val == null ){
		    log.error( "No label value found for '{}'", labelAttribute );
		    return;
		}
		
		Double label = null;
		
		if( val instanceof Double ){
		    label = (Double) val;
		} else {
		    log.error( "Only numerical labels {Ê-1.0d, +1.0d } are supported by this learner!" );
		    return;
		}
		
		if( label < 0.0d )
		    label = -1.0d;
		else
		    label = 1.0d;

		InputVector example = this.createSparseVector( item );

		//---reading label
		// ---start computation
		Double prediction = this.predict(example);
		if( prediction < 0.0d )
			prediction = -1.0d;
		else
			prediction = 1.0d;
		
		if ( prediction * label < 0 ) {
			double direction = label;
			beta0 = beta0 + ( learnRate * direction );
			beta = beta.add( learnRate * direction, example );
		}
	}

	
	
	public Double predict( InputVector example ){
		double pred = beta0 + example.innerProduct( beta );
		return pred;
	}
	

	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public Double predict(Data item) {
		InputVector example = createSparseVector( item );
		if( predict( example ) < 0.0d )
			return -1.0d;
		else
			return 1.0d;
	}

	@Override
	public double getByteSize() {
		//
		// size of beta0 (64 bit)
		// + size of weight-vector beta
		//
		return  8.0d + beta.getByteSize();
	}
	
	
	public void printModel(){
		log.info( "----------------------------------------" );
		log.info( "beta0 = {}", beta0 );
		log.info( "beat = {}", beta );
		log.info( "----------------------------------------" );
	}
}