/**
 * 
 */
package stream.plugin.processing.convert;

import java.util.List;

import stream.plugin.DataObject;
import stream.plugin.DefaultOperator;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.InputPortExtender;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 *
 */
public class MergeDataObjectOperator extends DefaultOperator {

	//InputPort input = getInputPorts().createPort( "data item" );
	InputPortExtender mergePorts = new InputPortExtender( "data item", getInputPorts() ); //, null, 2 );
	
	OutputPort output = getOutputPorts().createPort( "data item" );
	
	/**
	 * @param description
	 */
	public MergeDataObjectOperator(OperatorDescription description) {
		super(description);
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		
		List<InputPort> inputs = mergePorts.getManagedPorts();
		
		InputPort input = inputs.get(  0 );
		DataObject item = input.getData( DataObject.class );
		
		
		for( int i = 1; i < inputs.size(); i++ ){ // InputPort port : mergePorts.getManagedPorts() ){
			InputPort port = inputs.get( i );
			if( port.isConnected() ){
				DataObject merge = port.getData( DataObject.class );
				if( merge != null ){
					item.putAll( merge.getWrappedDataItem() );
				}
			}
		}
		
		output.deliver( item );
	}
}