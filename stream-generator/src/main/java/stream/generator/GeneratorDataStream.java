package stream.generator;

import java.util.ArrayList;
import java.util.List;

import stream.data.DataProcessor;
import stream.io.DataStream;

public abstract class GeneratorDataStream implements DataStream {

	final List<DataProcessor> processors = new ArrayList<DataProcessor>();
	

	@Override
	public void addPreprocessor(DataProcessor proc) {
		processors.add( proc );
	}

	@Override
	public void addPreprocessor(int idx, DataProcessor proc) {
		processors.add( idx, proc );
	}

	@Override
	public List<DataProcessor> getPreprocessors() {
		return processors;
	}

}
