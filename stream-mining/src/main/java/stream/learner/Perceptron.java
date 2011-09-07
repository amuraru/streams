package stream.learner;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.Measurable;
import stream.data.vector.SparseVector;
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
	List<String> labels = new ArrayList<String>();

	double beta0 = 0.0d;
	SparseVector beta = new SparseVector();

	
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

		Double label = null;
		if( item.get( labelAttribute ) == null ){
			log.error( "No label found for example!" );
			return;
		} else {
			label = new Double( item.get( labelAttribute ).toString() );
		}

		int labelIndex = labels.indexOf( Double.toString( label ) );
		if( labelIndex < 0  && labels.size() < 2 ){
			log.info( "Adding label '{}'", label );
			labels.add( label.toString() );
			labelIndex = labels.indexOf( Double.toString( label ) );
		} 

		SparseVector example = this.createSparseVector( item );

		//---reading label
		// ---start computation
		Double prediction = this.predict(example);
		if( prediction < 0.0d )
			prediction = -1.0d;
		else
			prediction = 1.0d;
		
		if ( prediction * label < 0 ) {
			double direction = (labelIndex == 0) ? -1.0 : 1.0;
			beta0 = beta0 + ( learnRate * direction );
			beta = beta.add( learnRate * direction, example );
		}
	}

	
	
	public Double predict( SparseVector example ){
		double pred = beta0 + example.innerProduct( beta );
		return pred;
	}
	

	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public Double predict(Data item) {
		if( labels.isEmpty() ){
			log.warn( "No labels available, predicting '?'!" );
			return Double.NaN;
		}

		if( labels.size() == 1 ){
			log.warn( "Only 1 label available, predicting '{}'!", labels.get( 0 ) );
			return -1.0d;
		}

		SparseVector example = createSparseVector( item );
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