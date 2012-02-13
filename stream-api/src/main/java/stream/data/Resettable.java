/**
 * 
 */
package stream.data;

/**
 * Processors implementing this interface provide a special
 * reset method allowing for resetting their state to some
 * initial state.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface Resettable {

	public void reset();
}
