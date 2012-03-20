/**
 * 
 */
package fact.plugin.operators;

import java.io.File;
import java.util.List;

import stream.data.Data;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;

import fact.data.DrsCalibration;
import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class DrsCalibrationOperator extends Operator {

	public final static String DRS_FILE = "Drs File";
	
	InputPort input = getInputPorts().createPort( "evt fact event" );
	OutputPort output = getOutputPorts().createPort( "evt fact event" );

	DrsCalibration drsCalibration;

	
	/**
	 * @param description
	 * @param clazz
	 */
	public DrsCalibrationOperator(OperatorDescription description) {
		super(description);
		
		this.acceptsInput( FactEventObject.class );
		this.producesOutput( FactEventObject.class );
	}

	
	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		FactEventObject object = input.getData( FactEventObject.class );

		if( drsCalibration == null ){
			
			File drsFile = getParameterAsFile( DRS_FILE );
			
			drsCalibration = new DrsCalibration();
			drsCalibration.setDrsFile( drsFile.getAbsolutePath() );
			drsCalibration.setKeepData( false );
		}
		
		Data calibratedEvent = drsCalibration.process( object );
		output.deliver( new FactEventObject( calibratedEvent ) );
	}

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeFile( DRS_FILE, "The DRS data file", null, false ) );
		return types;
	}
}