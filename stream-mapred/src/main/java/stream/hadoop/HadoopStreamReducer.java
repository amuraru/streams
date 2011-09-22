package stream.hadoop;

public interface HadoopStreamReducer<I,O> {

	public I read();
	
	public void write( O out );
}
