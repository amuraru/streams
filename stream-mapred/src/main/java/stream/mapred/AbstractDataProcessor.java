/**
 * 
 */
package stream.mapred;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;
import stream.io.DataStreamWriter;
import stream.io.SparseDataStreamWriter;
import stream.io.SvmLightDataStream;

/**
 * @author chris
 *
 */
public class AbstractDataProcessor {
	
	static Logger log = LoggerFactory.getLogger( AbstractDataProcessor.class );

	/* The input data stream */
	private DataStream dataInputStream;

	/* The output data stream */
	private DataStreamWriter dataOutputStream;
	
	
	/**
	 * Returns the data input stream for this mapper. The stream provides a one-pass
	 * access to the data block, currently processed by this mapper.
	 * 
	 * @return
	 */
	public DataStream getDataInputStream(){
		return dataInputStream;
	}
	
	
	/**
	 * Returns the data output stream for this mapper. The stream can be used to write
	 * any output of the mapper, that is subsequently processed by the reducer.
	 * 
	 * @return
	 */
	public DataStreamWriter getDataOutputStream(){
		return dataOutputStream;
	}
	
	
	/**
	 * This method creates the input data stream from a given input stream object. The
	 * default input stream to create is the SvmLightDataStream for reading data from
	 * file in SVMlight format.
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public DataStream createDataInputStream( InputStream in ) throws Exception {
		log.debug( "Creating svm-light-data-stream from {}", in );
		return new SvmLightDataStream( in );
	}


	/**
	 * This method creates the output data stream for sending any intermediate results
	 * to the following reducers. Be aware that the reducer class needs to use a data
	 * input stream that is able to read the output of the data stream writer returned
	 * by this method.
	 * 
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public DataStreamWriter createDataOutputStream( OutputStream out ) throws Exception {
		log.debug( "Creating sparse-data-strea-writer from {}", out );
		return new SparseDataStreamWriter( out );
	}
	
	
	public void init( InputStream in, OutputStream out ) throws Exception {
		dataInputStream = createDataInputStream( in );
		
		dataOutputStream = createDataOutputStream( out );
	}
	
	
	public Data read() throws Exception {
		return dataInputStream.readNext();
	}
	
	public void write( Data item ) throws Exception {
		dataOutputStream.process( item );
	}
}