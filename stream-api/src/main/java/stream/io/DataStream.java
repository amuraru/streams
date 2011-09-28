/**
 * 
 */
package stream.io;

import java.util.List;
import java.util.Map;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * <p>
 * A simple data stream interface, producing data items.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface DataStream {
	
	/**
	 * This method returns a mapping of attributes to types.
	 * 
	 * @return
	 */
	public Map<String,Class<?>> getAttributes();
	
	
	/**
	 * Returns the next datum from this stream.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Data readNext() throws Exception;
	
	
	public Data readNext( Data datum ) throws Exception;
	
	
	public void addPreprocessor( DataProcessor proc );
	
	public void addPreprocessor( int idx, DataProcessor proc );
	
	public List<DataProcessor> getPreprocessors();
}