/**
 * 
 */
package stream.plugin.sources;

import java.util.List;
import java.util.Map;

import stream.io.DataStream;
import stream.plugin.DataSourceObject;
import stream.plugin.util.OperatorUtils;
import stream.plugin.util.ParameterTypeDiscovery;
import stream.tools.StreamRunner;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;

/**
 * <p>
 * This class implements a simple DataStreamReader that will be equipped with 
 * a URL (parameter) and will provide a single, stateful data-stream object.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public abstract class DataStreamReader<D extends DataStream> extends Operator {

	public final static String INPUT_FILE = "url";
	public final static String LIMIT = "limit";
	final OutputPort output = getOutputPorts().createPort( "stream" );

	boolean setup = false;
	DataStream stream;
	Class<D> dataStreamClass;
	
	DataSourceObject dataSource = null;
	
	/**
	 * @param description
	 */
	public DataStreamReader(OperatorDescription description, Class<D> streamClass ) {
		super(description);
		dataStreamClass = streamClass;
		producesOutput( DataSourceObject.class );
	}

	
	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		super.doWork();
		
		if( dataSource == null ){
			try {
				Map<String,String> params = OperatorUtils.getParameters( this );
				stream = createDataStream( dataStreamClass, params );
				//dataSource = new DataSourceObject( stream );
				output.deliver( new DataSourceObject( stream ) );
			} catch (Exception e) {
				throw new OperatorException( "Failed to create data-source!", e );
			}
		}
	}

	
	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeFile( INPUT_FILE, "The file to read from", "", false ) );
		types.add( new ParameterTypeInt( LIMIT, "The maximum number of items read from the stream", -1, Integer.MAX_VALUE, true ) );
		
		Map<String,ParameterType> discovered = ParameterTypeDiscovery.discoverParameterTypes( dataStreamClass );
		for( String key : discovered.keySet() ){
			if( ! "url".equalsIgnoreCase( key ) && ! "limit".equalsIgnoreCase( key ) ){
				types.add( discovered.get( key ) );
			}
		}
		
		return types;
	}
	
	@SuppressWarnings("unchecked")
	public D createDataStream( Class<D> dataStreamClass, Map<String,String> parameters ) throws Exception {
		parameters.put( "class", dataStreamClass.getName() );
		try {	
			D stream = (D) StreamRunner.createStream( parameters );
			return stream;
		} catch (Exception e) {
			String url = parameters.get( "url" );
			parameters.put( "url", "file:" + url );
			D stream = (D) StreamRunner.createStream( parameters );
			return stream;
		}
	}
}
