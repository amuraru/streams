package stream.eval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.learner.Learner;
import stream.model.PredictionModel;

public class TestThenTrainProcessor
	implements DataProcessor 
{
	static Logger log = LoggerFactory.getLogger( TestThenTrainProcessor.class );
	
	List<Learner<Data,PredictionModel<Data,Serializable>>> learners = new ArrayList<Learner<Data,PredictionModel<Data,Serializable>>>();
	
	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		for( Learner<Data,PredictionModel<Data,Serializable>> learner : learners ){
			PredictionModel<Data,Serializable> model = learner.getModel();
			Serializable prediction = model.predict( data );
			data.put( "@pred:" + learner.toString(), prediction );
		}
		
		return data;
	}
}