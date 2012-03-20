/**
 * 
 */
package fact.plugin.operators;

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;

import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public abstract class AbstractFactEventOperator extends Operator {

	final InputPort input = getInputPorts().createPort( "evt fact event" );
	final OutputPort output = getOutputPorts().createPort( "evt fact event" );
	final List<ParameterType> types = new ArrayList<ParameterType>();
	
	/**
	 * @param description
	 */
	public AbstractFactEventOperator(OperatorDescription description) {
		super(description);
		this.acceptsInput( FactEventObject.class );
		this.producesOutput( FactEventObject.class );
	}

	
	public void doWork() throws OperatorException {
		try {
			FactEventObject event = input.getData( FactEventObject.class );
			FactEventObject processed = process( event );
			output.deliver( processed );
		} catch (Exception e) {
			throw new UserError( this, e, -1 );
		}
	}

	public void addParameterType( ParameterType type ){
		types.add( type );
	}
	
	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> pt = super.getParameterTypes();
		pt.addAll( types );
		return pt;
	}


	public abstract FactEventObject process( FactEventObject event ) throws Exception;
}
