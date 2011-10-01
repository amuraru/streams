/**
 * 
 */
package stream.mapred.learner;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataUtils;
import stream.data.vector.Vector;
import stream.learner.Perceptron;
import stream.mapred.StreamMapper;

/**
 * @author chris
 *
 */
public class PerceptronMapper extends StreamMapper {

	Perceptron perceptron;
	Double learnRate = 1.0e-3;

	
	/**
	 * @return the learnRate
	 */
	public Double getLearnRate() {
		return learnRate;
	}


	/**
	 * @param learnRate the learnRate to set
	 */
	public void setLearnRate(Double learnRate) {
		this.learnRate = learnRate;
	}


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
	public void finish() throws Exception {
		Data weights = new DataImpl();
		
		weights.put( "@id", "perceptron_weights[" + Thread.currentThread().getId() + "]" );
		Vector beta = perceptron.getWeightsVector();
		Double b = perceptron.getIntercept();
		
		DataUtils.put( weights, beta );
		weights.put( "b", b );
		
		write( weights );
	}
}