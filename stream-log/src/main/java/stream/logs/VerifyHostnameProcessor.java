/**
 * 
 */
package stream.logs;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * <p>
 * This processor implements a lookup and reverse-lookup check for
 * processed data items. The data items are expected to provide a
 * <code>REMOTE_ADDR</code> (changable) key that is used ad input
 * IP address.
 * </p>
 * <p>
 * The IP address is resolved to a hostname, which in turn is
 * again resolved to an IP address. The hostname will be stored as
 * <code>hostname:REMOTE_ADDR</code>, the resolve IP address will
 * be available as <code>address:hostname:REMOTE_ADDR</code>. 
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class VerifyHostnameProcessor 
	implements DataProcessor 
{
	String key = "REMOTE_ADDR";
	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		return null;
	}
}