/**
 * 
 */
package stream.plugin;

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 *
 */
public abstract class DefaultOperator extends Operator {

	final List<ParameterType> types = new ArrayList<ParameterType>();
	
	/**
	 * @param description
	 */
	public DefaultOperator(OperatorDescription description) {
		super(description);
		types.addAll( super.getParameterTypes() );
	}
	
	
	public void clearParameterTypes(){
		types.clear();
	}
	
	public void addParameterType( ParameterType type ){
		types.add( type );
	}
	
	public List<ParameterType> getParameterTypes(){
		return types;
	}
}
