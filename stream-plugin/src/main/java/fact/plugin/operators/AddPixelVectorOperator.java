/**
 * 
 */
package fact.plugin.operators;

import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;

import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class AddPixelVectorOperator extends Operator {

	public final static String COLUMN_NAME = "Column name";
	
	InputPort input = getInputPorts().createPort( "example set" );
	InputPort eventInput = getInputPorts().createPort( "evt fact event" );
	
	OutputPort output = getOutputPorts().createPort( "evt fact event" );
	
	
	/**
	 * @param description
	 */
	public AddPixelVectorOperator(OperatorDescription description) {
		super(description);
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		ExampleSet exampleSet = input.getData( ExampleSet.class );
		FactEventObject event = eventInput.getData( FactEventObject.class );

		String column = getParameterAsString( COLUMN_NAME );
		
		float[] vector = new float[ exampleSet.size() ];
		
		Attribute col = exampleSet.getAttributes().get( column );
		for( int i = 0; i < vector.length; i++ ){
			Example example = exampleSet.getExample( i );
			vector[i] = new Float(example.getValue( col ));
		}
		
		event.put( column, vector );
		output.deliver( event );
	}


	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeString( COLUMN_NAME, "", false ) );
		return types;
	}
}