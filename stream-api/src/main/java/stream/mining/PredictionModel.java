/**
 * 
 */
package stream.mining;

import java.io.Serializable;

import stream.data.Data;

/**
 * @author chris
 *
 */
public interface PredictionModel<T extends Serializable>
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
	public T predict( Data item );
}
