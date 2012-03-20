/**
 * 
 */
package fact.plugin.operators;

import com.rapidminer.operator.OperatorDescription;

/**
 * @author chris
 *
 */
public class SelectSlicesOperator extends AbstractFactEventProcessor {

	/**
	 * @param description
	 * @param clazz
	 */
	public SelectSlicesOperator(OperatorDescription description) {
		super(description, fact.data.CutSlices.class );
	}
}