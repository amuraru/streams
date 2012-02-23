/**
 * 
 */
package stream.plugin.processing.convert;

import java.util.Iterator;
import java.util.List;

import stream.data.Data;
import stream.data.DataImpl;
import stream.plugin.DataObject;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;

/**
 * @author chris
 *
 */
public class ExampleSet2Array extends Operator {

	public final static String KEY_PARAMETER = "Key";
	public final static String INCLUDE_SPECIAL = "include special attributes";

	final InputPort input = getInputPorts().createPort( "example set" );
	final OutputPort output = getOutputPorts().createPort( "data item" );
	
	
	/**
	 * @param description
	 */
	public ExampleSet2Array(OperatorDescription description) {
		super(description);
	}

	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		String key = getParameterAsString( KEY_PARAMETER );
		boolean includeSpecial = getParameterAsBoolean( INCLUDE_SPECIAL );
		

		ExampleSet exampleSet = input.getData( ExampleSet.class );
		Attributes attributes = exampleSet.getAttributes();
		
		int columns = attributes.size();
		if( includeSpecial )
			columns += attributes.specialSize();
		
		double[] array = new double[ exampleSet.size() * columns ];
		
		int row = 0;
		for( Example example : exampleSet ){
			
			Iterator<Attribute> cols;
			if( includeSpecial ){
				cols = attributes.allAttributes();
			} else {
				cols = attributes.iterator();
			}
			
			int c = 0;
			int offset = row * columns;
			while( cols.hasNext() ){
				double value = example.getValue( cols.next() );
				array[ offset + c ] = value;
				c++;
			}
			
			row++;
		}

		Data item = new DataImpl();
		item.put( key, array );

		output.deliver( new DataObject( item ) );
	}

	
	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeString( KEY_PARAMETER, "", false ) );
		types.add( new ParameterTypeBoolean( INCLUDE_SPECIAL, "", false ) );
		return types;
	}
}