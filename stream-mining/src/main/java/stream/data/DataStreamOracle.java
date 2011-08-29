package stream.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.learner.Learner;
import stream.model.PredictionModel;

/**
 * The data stream oracle is a simple data processor that is based upon
 * a learner (a learner needs to be provided) and simply adds the prediction
 * of this learner to all data items that have been presented to the processor.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class DataStreamOracle 
	implements DataProcessor 
{
	static Logger log = LoggerFactory.getLogger( DataStreamOracle.class );
	
	/* The learning algorithm of this oracle */
	Learner<Data,PredictionModel<Data,Serializable>> learner;
	
	
	public DataStreamOracle( Learner<Data,PredictionModel<Data,Serializable>> learner ){
		this.learner = learner;
	}
	
	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data arg0) {
		log.debug( "Processing datum {}", arg0 );
		PredictionModel<Data,Serializable> model = learner.getModel();
		Serializable prediction = model.predict( arg0 );

		for( String key : arg0.keySet() ){
			if( key.startsWith( "@label" ) ){
				log.debug( "   data is labeled as:  {} = {}", key, arg0.get( key ) );
			}
		}
		
		String predLabel = "@pred:" + model.toString();
		log.debug( "   oracle's prediction is: {} = {}", predLabel, prediction );
		arg0.put( predLabel, prediction );
		return arg0;
	}
}