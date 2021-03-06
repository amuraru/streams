package stream.learner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.model.HyperplaneModel;
import edu.udo.cs.pg542.util.Kernel;

/**
 * Perceptron stream learner
 * 
 * @author Helge Homburg, Christian Bockermann
 */
public class PerceptronRegressor
	extends AbstractRegressor<Data>
{
	private static final long serialVersionUID = -3263838547557335984L;

	static Logger log = LoggerFactory.getLogger( PerceptronRegressor.class );

	/* The learning rate gamma */
	Double learnRate;

	/* The label attribute */
	String labelAttribute;

	/* The default labels predicted by this model */
	List<String> labels = new ArrayList<String>();

	/* The attribute which this learner acts upon */
	List<String> attributes = new ArrayList<String>();

	private HyperplaneModel model;    



	public PerceptronRegressor() {
		this(Kernel.INNER_PRODUCT, 0.05);
	}

	public PerceptronRegressor(int learnRate) {
		this(Kernel.INNER_PRODUCT, learnRate);
	}

	public PerceptronRegressor(int kernelType, double learnRate) {       
		this.model = new HyperplaneModel(kernelType);
		this.model.initModel( new LinkedHashMap<String,Double>(), 0.0d );
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

		String label = null;
		if( item.get( labelAttribute ) == null ){
			log.error( "No label found for example!" );
			return;
		} else {
			label = item.get( labelAttribute ).toString();
		}

		int labelIndex = labels.indexOf( label );
		if( labelIndex < 0  && labels.size() < 2 ){
			log.info( "Adding label '{}'", label );
			labels.add( label );
			labelIndex = labels.indexOf( label );
		} 
		
		if( labelIndex < 0 ){
			log.error( "My labels are {}, unknown label: {}", labels, label );
			if( labels.size() == 2 )
				log.error( "The perceptron algorithm only works for binary classification tasks!" );
			return;
		}


		Map<String,Double> example = LearnerUtils.getNumericVector( item );
		if( example.isEmpty() ){
			log.info( "No numerical attributes found for learning! Ignoring example!" );
			return;
		}

		//---reading label
		// ---start computation
		Double prediction = model.predict(item);            
		if (prediction != null && prediction.intValue() != labelIndex ) {            		
			double direction = (labelIndex == 0) ? -1 : 1;
			// adjusting bias
			model.setBias(model.getBias() + (learnRate * direction));

			// adjusting models weights
			Map<String,Double> weights = model.getWeights();
			for (String attribute : attributes ) {
				Double attributeValue = example.get( attribute );
				Double weight = weights.get( attribute );
				if( weight == null )
					weight = 0.0d;

				weight += learnRate * direction * attributeValue;
				weights.put( attribute, weight );
			}
			model.setWeights(weights);			
		}
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
		Double pred = model.predict( item );
		return pred;
	}
}