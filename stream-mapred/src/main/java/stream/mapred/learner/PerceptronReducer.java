/**
 * 
 */
package stream.mapred.learner;

import stream.data.Data;
import stream.learner.Perceptron;
import stream.mapred.StreamMapper;

/**
 * @author chris
 *
 */
public class PerceptronReducer extends StreamMapper {

	Perceptron perceptron;
	Double learnRate = 1.0e-3;
	
	
	/**
	 * @see stream.mapred.Mapper#init()
	 */
	@Override
	public void init() {
		perceptron = new Perceptron();
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		perceptron.learn( data );
		return data;
	}

	
	/**
	 * @see stream.mapred.Mapper#finish()
	 */
	@Override
	public void finish() {
		
	}
}