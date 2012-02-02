/**
 * 
 */
package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * <p>
 * A simple processor which adds sequential IDs to all processed
 * data items. The IDs start at 1 (first ID).
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class AddId 
	implements DataProcessor 
{
	/** The state of this processor (the next ID to assign) */
	Long nextId = 1L;
	
	/** The key used to plant the ID */
	String key = "@id";

	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}



	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		data.put( key, nextId );
		nextId++;
		
		return data;
	}
}