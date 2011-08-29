/**
 * 
 */
package stream.learner.tree;

/**
 * @author chris
 *
 */
public class Range {
	Double min = Double.NEGATIVE_INFINITY;
	Double max = Double.POSITIVE_INFINITY;

	
	/**
	 * 
	 */
	public Range( Double min, Double max ){
		this.min = min;
		this.max = max;
	}
	
	public boolean includes( Double d ){
		return min < d && d <= max;
	}
}