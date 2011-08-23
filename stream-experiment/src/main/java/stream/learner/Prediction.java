/**
 * 
 */
package stream.learner;

import stream.data.Data;

/**
 * @author chris
 *
 */
public interface Prediction {

	public final static String LABEL_PREFIX = Data.ANNOTATION_PREFIX + "label";
	
	public final static String PREDICTION_PREFIX = Data.ANNOTATION_PREFIX + "pred:";
	public final static String CONFIDENCE_PREFIX = Data.ANNOTATION_PREFIX + "conf:";
}