/**
 * 
 */
package stream.learner;

import stream.data.Data;
import stream.model.PredictionModel;
import stream.model.TrueLabelModel;

/**
 * <p>
 * This learner does not learn anything and is used to simply return the label
 * of labeled data. It is a useful learner as baseline to evaluate different
 * learning algorithms against the true label.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class LabelPredictor<T> extends TrueLabelModel<T> implements Learner<Data, PredictionModel<Data,T>> {

	/** The unique class ID */
	private static final long serialVersionUID = -67867885352106994L;

	String labelAttribute;

	/**
	 * @see stream.learner.Learner#getModel()
	 */
	@Override
	public PredictionModel<Data, T> getModel() {
		return this;
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
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn(Data item) {
	}
}