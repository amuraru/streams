package stream.hadoop;

public interface StreamReducer<I,O> {

	public I read();
	
	public void write( O out );
}
