/**
 * 
 */
package stream.data;

/**
 * <p>
 * This interface provides a simple processing unit for streaming data. Processing
 * can be either read-only or with altering of the data.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface DataProcessor 
	extends Processor<Data,Data> 
{

	/**
	 * Process the given unit of data. 
	 * 
	 * @param data The data item to be processed.
	 * @return     The data after being processed.
	 */
	public Data process( Data data );
}
