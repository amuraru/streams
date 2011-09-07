package stream.eval;

import java.util.LinkedHashMap;
import java.util.Map;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.learner.Learner;

public class TrainProcessor implements DataProcessor {

	Map<String,Learner<Data,?>> learners = new LinkedHashMap<String,Learner<Data,?>>();

	
	public void addLearner( String name, Learner<Data,?> learner ){
		learners.put( name, learner );
	}
	
	
	public Learner<Data,?> removeLearner( String name ){
		return learners.remove( name );
	}
	
	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		for( String key : learners.keySet() ){
			learners.get( key ).learn( data );
		}		
		return data;
	}
}