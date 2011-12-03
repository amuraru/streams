/**
 * 
 */
package stream.mining;

import stream.data.DataProcessor;

/**
 * This interface defines a simple abstract Learner. A learner is an
 * object that processed data (during learning) and can return a prediction
 * model.
 * 
 * In the stream-mining case, learners are any-time algorithms, i.e. the
 * model is constantly evolving, and can be accessed at any point in time.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface Learner extends DataProcessor {

	/**
	 * This method is called at initialization time. It will reset
	 * the state of the learning algorithm.
	 * 
	 */
	public void init();
	
	
	/**
	 * This method provides any-time access to the prediction model
	 * created by the implementing learner class.
	 * 
	 * @return
	 */
	public PredictionModel<?> getModel();
}