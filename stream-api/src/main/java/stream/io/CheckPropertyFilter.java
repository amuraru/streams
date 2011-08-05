/**
 * 
 */
package stream.io;

import java.util.Map;

/**
 * @author chris
 *
 */
public class CheckPropertyFilter implements DataFilter {
	String prop;
	
	public CheckPropertyFilter( String property ){
		prop = property;
	}
	
	/* (non-Javadoc)
	 * @see stream.io.DataFilter#matches(java.util.Map)
	 */
	@Override
	public boolean matches(Map<String, ?> datum) {
		return datum.containsKey( prop );
	}
}