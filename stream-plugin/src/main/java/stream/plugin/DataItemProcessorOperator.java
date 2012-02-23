/**
 * 
 */
package stream.plugin;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 *
 */
public abstract class DataItemProcessorOperator extends DefaultOperator {

	protected InputPort input = getInputPorts().createPort( DataStreamPlugin.DATA_ITEM_PORT_NAME );
	protected OutputPort output = getOutputPorts().createPort( DataStreamPlugin.DATA_ITEM_PORT_NAME );
	
	/**
	 * @param description
	 */
	public DataItemProcessorOperator(OperatorDescription description) {
		super(description);
	}
	
	
	public abstract DataObject process( DataObject object );
}