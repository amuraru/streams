/**
 * 
 */
package fact.plugin;

import java.util.List;

import stream.data.DataProcessor;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;

/**
 * <p>
 * This generic operator can be used as super class wrapper for DataProcessor
 * classes. It will generate the list of DataProcessor parameters via reflection
 * and inject these into the implementing DataProcessor class upon initialization.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class GenericFactOperator extends Operator {

	DataProcessor processor;
	
	/**
	 * @param description
	 */
	public GenericFactOperator(OperatorDescription description) {
		super(description);
	}

	
	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		return types;
	}
}