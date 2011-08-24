package stream.io;

public interface Parser<T> {

	public T parse( String input ) throws Exception;
}
