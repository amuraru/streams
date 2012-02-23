/**
 * 
 */
package stream.plugin.processing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.ListDataStream;
import stream.plugin.DataObject;
import stream.plugin.DataSourceObject;
import stream.plugin.DataStreamOperator;
import stream.plugin.DataStreamPlugin;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;


/**
 * <p>
 * This operator is a simple operator chain that will execute a <b>one-pass</b>
 * iteration over the input example set. Any operators that will be added to this
 * chain need to be able to deal with that. 
 * </p>
 * <p>
 * Thus, it does not make sense to put in any learners that may require multiple
 * passes over the training data.
 * </p>
 * 
 * @author Christian Bockermann
 *
 */
public class DataStreamProcess extends AbstractDataStreamProcess<DataSourceObject,DataObject> {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( DataStreamProcess.class );
	
	List<DataObject> resultBuffer = new ArrayList<DataObject>();
	
	
	/**
	 * @param description
	 */
	public DataStreamProcess(OperatorDescription description) {
		super(description, "Process Data Stream", DataStreamPlugin.DATA_STREAM_PORT_NAME, DataSourceObject.class, DataStreamPlugin.DATA_ITEM_PORT_NAME );
	}
	

	/**
	 * @see com.rapidminer.operator.OperatorChain#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		resultBuffer.clear();

		List<Operator> nested = this.getImmediateChildren();
		log.debug( "This StreamProcess has {} nested operators", nested.size() );
		for( Operator op : nested ){
			log.debug( "  op: {}", op );
			
			if( op instanceof DataStreamOperator ){
				log.debug( "Resetting stream-operator {}", op );
				((DataStreamOperator) op).reset();
			}
		}

		
		log.debug( "Starting some work in doWork()" );
		DataSourceObject dataSource = input.getData( DataSourceObject.class );
		log.debug( "input is a data-stream-source..." );
		int i = 0;
		

		Data item = dataSource.readNext();
		while( item != null ){
			log.trace( "Processing example {}", i );
			DataObject datum = dataSource.wrap( item );
			log.trace( "Wrapped data-object is: {}", datum );
			dataStream.deliver( datum );
			getSubprocess(0).execute();
			inApplyLoop();
			i++;
			
			try {
				DataObject processed = outputStream.getData( DataObject.class );
				if( processed != null && output.isConnected() ){
					log.debug( "Adding processed data item: {}", processed.getWrappedDataItem() );
					resultBuffer.add( processed );
				}
			} catch (Exception e) {
				log.error( "Failed to retrieve processed data-item: {}", e.getMessage()  );
			}
			
			item = dataSource.readNext();
		}
		
		if( output.isConnected() ){
			log.debug( "Collected {} data items as result." );
			output.deliver( new DataSourceObject( new ListDataStream( resultBuffer ) ) );
		}

		log.debug( "doWork() is finished." );
	}


	/**
	 * @see stream.plugin.processing.AbstractDataStreamProcess#wrap(stream.data.Data)
	 */
	@Override
	public DataObject wrap(Data item) {
		return new DataObject( item );
	}


	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new ArrayList<ParameterType>(); // super.getParameterTypes();
		for( ParameterType type : types ){
			log.info( "Found parameter '{}' with description '{}'", type.getKey(), type.getDescription() );
		}
		return types;
	}
}