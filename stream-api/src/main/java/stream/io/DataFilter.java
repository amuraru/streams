/**
 * 
 */
package stream.io;

import java.util.Map;

/**
 * This interface defines an abstract methodology to filter out unwanted
 * data items.
 * 
 * @author chris
 *
 */
public interface DataFilter {

	public boolean matches( Map<String,?> datum );
}
