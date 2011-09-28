package stream.hadoop;

import stream.data.DataProcessor;

public interface StreamMapper
	extends DataProcessor
{
	
	public void init();
	
	public void finish();
}
