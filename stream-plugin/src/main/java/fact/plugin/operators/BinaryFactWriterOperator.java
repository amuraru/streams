/**
 * 
 */
package fact.plugin.operators;

import java.io.File;
import java.util.List;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;

import fact.io.BinaryFactWriter;
import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class BinaryFactWriterOperator extends Operator {

	public final static String OUTPUT_FILE = "Output file";
	final InputPort input = getInputPorts().createPort( "evt fact event" );

	/**
	 * @param description
	 */
	public BinaryFactWriterOperator(OperatorDescription description) {
		super(description);
		this.acceptsInput( FactEventObject.class );
	}

	/* (non-Javadoc)
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
	
		File file = getParameterAsFile( OUTPUT_FILE );
		
		BinaryFactWriter writer = new BinaryFactWriter();
		writer.setFile( file.getAbsolutePath() );

		FactEventObject event = input.getData( FactEventObject.class );
		writer.process( event );
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeFile( OUTPUT_FILE, "", "dat", false ) );
		return types;
	}
}