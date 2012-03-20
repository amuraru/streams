/**
 * 
 */
package fact.plugin.operators;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeFile;

import fact.data.DrsCalibration;
import fact.io.FactDataStream;
import fact.plugin.FactEventStream;

/**
 * @author chris
 *
 */
public class FactEventReader extends Operator {

	public final static String FITS_DATA_FILE = "Fits file";
	public final static String FITS_DRS_FILE = "Drs file";
	public final static String KEEP_UNCALIBRATED = "Keep uncalibrated data";

	final OutputPort output = getOutputPorts().createPort( "fact event stream" );

	/**
	 * @param description
	 */
	public FactEventReader(OperatorDescription description) {
		super(description);
		producesOutput( FactEventStream.class );
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {

		try {
			File fitsFile = getParameterAsFile( FITS_DATA_FILE );
			URL fitsData = fitsFile.toURI().toURL();
			FactDataStream stream = new FactDataStream( fitsData );

			File drsFile = getParameterAsFile( FITS_DRS_FILE );
			if( drsFile != null ){
				DrsCalibration drs = new DrsCalibration();
				drs.setDrsFile( drsFile.getAbsolutePath() );
				//stream.setDrsFile( drsFile.getAbsolutePath() );
				
				Boolean b = getParameterAsBoolean( KEEP_UNCALIBRATED );
				drs.setKeepData( b );
				stream.addPreprocessor( drs );
			}
			
			FactEventStream feStream = new FactEventStream( stream );
			output.deliver( feStream );
			
		} catch (Exception e) {
			throw new UserError( this, e, -1 );
		}
	}


	/* (non-Javadoc)
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeFile( FITS_DATA_FILE, "The fits file to read from", null, false ) );
		types.add( new ParameterTypeFile( FITS_DRS_FILE, "The DRS file for calibration", null, true ) );
		types.add( new ParameterTypeBoolean( KEEP_UNCALIBRATED, "Keep uncalibrated data", false ) );
		return types;
	}
}