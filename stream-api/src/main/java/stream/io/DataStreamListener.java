/**
 * 
 */
package stream.io;

import stream.data.Data;

/**
 * @author chris
 *
 */
public interface DataStreamListener {

	public void dataArrived( Data datum );
}
