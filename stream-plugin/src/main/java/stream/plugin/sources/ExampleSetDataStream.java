/**
 * 
 */
package stream.plugin.sources;

import stream.plugin.DataSourceObject;
import stream.plugin.util.ExampleSetDataStreamWrapper;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 *
 */
public class ExampleSetDataStream extends Operator {

	final InputPort input = getInputPorts().createPort( "example set" );
	
	final OutputPort output = getOutputPorts().createPort( "stream" );
	
	
	/**
	 * @param description
	 */
	public ExampleSetDataStream(OperatorDescription description) {
		super(description);
		
		this.acceptsInput( ExampleSet.class );
		this.producesOutput( DataSourceObject.class );
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		ExampleSet exampleSet = input.getData( ExampleSet.class );
		if( exampleSet != null ){
			ExampleSetDataStreamWrapper wrapper = new ExampleSetDataStreamWrapper( exampleSet );
			output.deliver( new DataSourceObject( wrapper ) );
		}
	}
}