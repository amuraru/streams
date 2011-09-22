package stream.hadoop;

import java.util.List;

import stream.data.Data;

public interface HadoopStreamMapper {
	
	public void init();
	
	public void process( List<Data> items );
	
	public void finish();
}
