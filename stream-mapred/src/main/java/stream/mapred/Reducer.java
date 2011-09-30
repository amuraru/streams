package stream.mapred;

public interface Reducer<I,O> {

	public I read();
	
	public void write( O out );
}
