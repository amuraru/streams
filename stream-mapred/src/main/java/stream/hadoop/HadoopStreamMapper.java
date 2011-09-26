package stream.hadoop;

import java.util.List;

import stream.data.Data;
import stream.data.DataProcessor;

public interface HadoopStreamMapper
	extends DataProcessor
{
	
	public void init();
	
	public void process( List<Data> items );
	
	public void finish();
}
