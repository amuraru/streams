/**
 * 
 */
package stream.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author chris
 *
 * @param <T>
 */
public interface DistributionModel<T extends Serializable> {
	
	/**
	 * Returns a histogram of this distribution.
	 * 
	 * @return
	 */
	public abstract Map<T, Integer> getHistogram();

	
	/**
	 * Returns the total number of all observations incorporated into
	 * this model.
	 * 
	 * @return
	 */
	public abstract Integer getCount();


	/**
	 * This method is called to incorporate a new observation into
	 * the distribution model.
	 * 
	 * @param item
	 */
	public abstract void update( T item );
}