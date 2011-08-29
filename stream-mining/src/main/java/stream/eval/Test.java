/**
 * 
 */
package stream.eval;

import java.util.Map;

import stream.data.stats.Statistics;
import stream.learner.Learner;

/**
 * @author chris
 *
 * @param <D>
 * @param <L>
 */
public interface Test<D, L extends Learner<D, ?>> {

	/**
	 * @see stream.eval.Evaluation#test(D)
	 */
	public abstract Statistics test( Map<String,L> learner, D data );

}