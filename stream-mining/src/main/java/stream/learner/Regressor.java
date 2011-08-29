/**
 * 
 */
package stream.learner;

/**
 * <p>
 * A regressor is a classifier with the target variable being a real value. 
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface Regressor<D> 
	extends Classifier<D, Double> 
{

}
