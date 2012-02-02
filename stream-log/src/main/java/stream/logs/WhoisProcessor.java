/**
 * 
 */
package stream.logs;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * <p>
 * This class implements a simple whois lookup processor. Each data item
 * processed by this instance will be checked for a REMOTE_ADDR property,
 * which is then used for looking up information about the network block,
 * the organization owning that block, etc.
 * </p>
 * <p>
 * The result of the lookup will be stored in variables like <code>WHOIS:ORG</code>,
 * <code>WHOIS:NETWORK</code>,...
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class WhoisProcessor 
	implements DataProcessor {

	String key = "REMOTE_ADDR";
	
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
		
		Serializable value = data.get( key );
		if( value == null ){
			return data;
		}
		
		return data;
	}
}