/**
 * 
 */
package stream.mining;

import java.io.Serializable;

/**
 * @author chris
 *
 */
public interface PredictionModel<T extends Serializable, D>
	extends Model
{
	/**
	 * This method performs the prediction according to the
	 * model state (hyperplane, probability distribution,..)
	 * for the given data item.
	 * 
	 * @param item
	 * @return
	 */
	public D predict( T item );
}
