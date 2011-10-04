package stream.mapred;

import java.io.InputStream;
import java.io.OutputStream;

import stream.data.DataProcessor;
import stream.io.DataStream;
import stream.io.DataStreamWriter;


/**
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public interface Reducer
	extends DataProcessor
{

	public void init() throws Exception;
	
	public void finish() throws Exception;
	
	public void reduce() throws Exception;
	
	
	
	/**
	 * This method is used to create the data input stream for this reducer.
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public DataStream createDataInputStream( InputStream in ) throws Exception;
	
	
	/**
	 * This method is used to create the data output stream for this reducer.
	 * 
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public DataStreamWriter createDataOutputStream( OutputStream out ) throws Exception;
}
