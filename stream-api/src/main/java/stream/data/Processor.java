/**
 * 
 */
package stream.data;

/**
 * @author chris
 *
 */
public interface Processor<I, O> {

	public O process( I input );
}
