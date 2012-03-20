/**
 * 
 */
package fact.plugin;

import stream.io.DataStream;
import stream.plugin.DataSourceObject;


/**
 * @author chris
 *
 */
public class FactEventStream extends DataSourceObject {

	/** The unique class ID */
	private static final long serialVersionUID = 1611811690868611841L;


	/**
	 * @param stream
	 */
	public FactEventStream(DataStream stream) {
		super(stream);
	}

	
	public FactEventObject nextFactEvent(){
		return null;
	}
}