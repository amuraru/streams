package stream.mapred;

import java.io.InputStream;
import java.io.OutputStream;

import stream.data.Data;
import stream.io.DataStream;
import stream.io.DataStreamFactory;
import stream.io.DataStreamWriter;
import stream.io.SparseDataStream;

public abstract class StreamReducer
	extends AbstractDataProcessor
	implements Reducer 
{
	
	/**
	 * @see stream.mapred.AbstractDataProcessor#createDataInputStream(java.io.InputStream)
	 */
	@Override
	public DataStream createDataInputStream(InputStream in) throws Exception {
		return new SparseDataStream( in );
	}

	
	
	
	/**
	 * @see stream.mapred.AbstractDataProcessor#createDataOutputStream(java.io.OutputStream)
	 */
	@Override
	public DataStreamWriter createDataOutputStream(OutputStream out) throws Exception {
		return DataStreamFactory.createDataOutputStream( System.getProperty( "reducer.output.format"), out );
	}




	public void reduce( InputStream in, OutputStream out ) throws Exception {
		init( in, out );
		
		reduce();
	}
	
	public void reduce() throws Exception {
		init();
		
		Data item = read();
		while( item != null ){
			process( item );
			item = read();
		}
		
		finish();
	}
}