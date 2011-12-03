/**
 * 
 */
package stream.mining;

import java.util.Set;

import stream.data.DataProcessor;

/**
 * @author chris
 *
 */
public interface Counter<T> extends DataProcessor {

	public CountModel<T> getModel();
	
	public Set<T> keySet();

	public Long getCount( T val );
	
	public Long getTotalCount();
	
	public void count( T value );
}
