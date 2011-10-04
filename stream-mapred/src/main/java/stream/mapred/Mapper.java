package stream.mapred;

import java.io.InputStream;
import java.io.OutputStream;

import stream.data.DataProcessor;
import stream.io.DataStream;
import stream.io.DataStreamWriter;


/**
 * This interface defines a simple abstract mapper class. The mapper's init() method
 * is called at the beginning of processing a block of data. After the block has been
 * processed, the <code>finish()</code> method will be called.
 * 
 * The interface extends the <code>DataProcessor</code> interface, which provides a
 * single <code>process(Data)</code> method, that will be called for each example of
 * the data block.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public interface Mapper
	extends DataProcessor
{
	/**
	 * This method is called at the start of processing a block of data. It can
	 * be used to restore some state or initialize basic data structures of the
	 * mapper implementation.
	 */
	public void init() throws Exception;

	
	/**
	 * This method is called after all data elements of the block have been processed.
	 * Usually this method is responsible for producing the output of a mapper and
	 * writing that.
	 */
	public void finish() throws Exception;
	
	
	/**
	 * This method is used to create the data input stream for this mapper.
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public DataStream createDataInputStream( InputStream in ) throws Exception;
	
	
	/**
	 * This method is used to create the data output stream for this mapper.
	 * 
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public DataStreamWriter createDataOutputStream( OutputStream out ) throws Exception;
}