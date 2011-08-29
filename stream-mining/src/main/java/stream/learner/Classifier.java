/**
 * 
 */
package stream.learner;

import stream.model.PredictionModel;

/**
 * <p>
 * A classifier is basically just a PredictionModel and the associated
 * learning algorithm. This interface defines that union.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface Classifier<D, T> 
	extends PredictionModel<D, T>, Learner<D, PredictionModel<D, T>> {
}
