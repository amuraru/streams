package stream.mapred;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public abstract class StreamReducer<I,O>
	implements Reducer<I,O> 
{
	PrintWriter writer;

	public void init( InputStream in, OutputStream out ){
		writer = new PrintWriter( out );
	}
	
	public abstract void reduce();
	
	public PrintWriter getWriter(){
		return writer;
	}
	
	public final void reduce( InputStream in, OutputStream out ){
		init( in, out );
		reduce();
		getWriter().flush();
		getWriter().close();
	}
}