package stream.experiment;

import java.io.OutputStream;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.learner.Learner;

public class HadoopTrainer 
	extends HadoopMapper
	implements DataProcessor
{
	Learner<Data,?> learner;
	
	public HadoopTrainer( Learner<Data,?> learner ){
		this.learner = learner;
	}

	@Override
	public Data process(Data data) {
		learner.learn( data );
		return data;
	}
	
	public void writeResults( OutputStream out ){
		
	}
}
