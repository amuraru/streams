/**
 * 
 */
package stream.eval;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.stats.Statistics;
import stream.learner.LearnerUtils;
import stream.learner.Prediction;

/**
 * <p>
 * This very simple class checks for label attributes and prediction
 * attributes and records all their differences.
 * </p>
 * 
 * @author Christian Bockermann
 *
 */
public class PredictionError 
	implements DataProcessor {

	static Logger log = LoggerFactory.getLogger( PredictionError.class );

	/* This class is used to record the prediction error */
	private final Statistics errors;
	
	boolean forwardError = false;
	
	
	
	public PredictionError(){
		errors = new Statistics();
		errors.setName( "PredictionError" );
	}
	

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		String labelKey = null;
		Serializable labelValue = null;
		for( String key : data.keySet() ){
			if( key.startsWith( Prediction.LABEL_PREFIX ) ){
				labelKey = key;
				labelValue = data.get( key );
				break;
			}
		}

		if( labelKey == null || labelValue == null ){
			log.warn( "No label found in data item!" );
			return data;
		}

		//
		// based on the label we next compute the prediction errors
		// for all predictions found in this data item
		//
		Set<String> predictions = new LinkedHashSet<String>();
		
		for( String key : data.keySet() ){
			if( key.startsWith( Prediction.PREDICTION_PREFIX ) ){
				predictions.add( key );
				
				String learnerName = this.extractLearnerName( key );
				String errorKey = Data.ANNOTATION_PREFIX + "predictionError[" + learnerName + "," + labelKey + "]";
				Serializable prediction = data.get( key );
				
				Double err = 0.0d;
				
				if( LearnerUtils.isNumerical( labelValue ) && LearnerUtils.isNumerical( prediction ) )
					err = error( (Double) labelValue, (Double) prediction );
				else
					err = error( labelValue, prediction );
				
				errors.add( errorKey, err );
				
				if( forwardError ){
					log.debug( "Forwarding error '{}' = {}", errorKey, err );
					data.put( errorKey, err );
				}
			}
		}
		
		return data;
	}
	

	/**
	 * 
	 * @param label
	 * @param pred
	 * @return
	 */
	protected Double error( Serializable label, Serializable pred ){
		
		if( label == null || pred == null && label != pred )
			return 1.0d;
		
		if( label == pred || label.toString().equals( pred.toString() ) )
			return 0.0d;
		return 1.0d;
	}
	
	
	protected Double error( Double label, Double pred ){
		return Math.abs( label - pred );
	}
	
	
	protected String extractLearnerName( String key ){
		if( key.startsWith( Prediction.PREDICTION_PREFIX ) ){
			return key.substring( Prediction.PREDICTION_PREFIX.length() );
		} else
			log.debug( "Key '{}' is not an annotation!" );
		
		return null;
	}
}