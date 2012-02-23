/**
 * 
 */
package stream.plugin;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;


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
public class StreamProcess extends OperatorChain {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( StreamProcess.class );
	
	/* The input port should provide a example set */
	InputPort input = getInputPorts().createPort( "example set" );
	
	/* The output port where we might deliver some output */
	//OutputPort output = getOutputPorts().createPort( "data stream" );

	final OutputPort dataStream = getSubprocess( 0 ).getInnerSources().createPort( DataStreamPlugin.DATA_STREAM_PORT_NAME );
	final InputPort outputStream = getSubprocess( 0 ).getInnerSinks().createPort( DataStreamPlugin.DATA_STREAM_PORT_NAME );
	
	/**
	 * @param description
	 */
	public StreamProcess(OperatorDescription description) {
		super(description, "Data Stream" );
	}
	

	/**
	 * @see com.rapidminer.operator.OperatorChain#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		List<Operator> nested = this.getImmediateChildren();
		log.info( "This StreamProcess has {} nested operators", nested.size() );
		for( Operator op : nested ){
			log.info( "  op: {}", op );
			
			if( op instanceof DataStreamOperator ){
				log.info( "Resetting stream-operator {}", op );
				((DataStreamOperator) op).reset();
			}
		}

		
		log.info( "Starting some work in doWork()" );
		ExampleSet exampleSet = input.getData( ExampleSet.class );
		log.info( "input is an example set with {} examples", exampleSet.size() );
		int i = 0;
		
		Iterator<Example> it = exampleSet.iterator();
		while( it.hasNext() ){
			Example example = it.next();
			log.info( "Processing example {}", i );
			DataObject datum = StreamUtils.wrap( example );
			log.info( "Wrapped data-object is: {}", datum );
			dataStream.deliver( datum );
			getSubprocess(0).execute();
			inApplyLoop();
			i++;
		}
		
		//super.doWork();
		log.info( "doWork() is finished." );
	}
}