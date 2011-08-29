/**
 * 
 */
package stream.eval;

/**
 * <p>
 * This interface defines an abstract loss function.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface LossFunction<T> {
	
	/**
	 * Compute the loss between x1 and x2.
	 * 
	 * @param x1
	 * @param x2
	 * @return
	 */
	public double loss( T x1, T x2 );
}
