/**
 * 
 */
package stream.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * This class implements a central factory for creating different data streams
 * from input/output streams.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class DataStreamFactory {

	
	
	
	public static DataStreamWriter createDataOutputStream( String format, OutputStream out ) throws Exception {
		if( format != null && "csv".equalsIgnoreCase( format.trim() ) )
			return new DataStreamWriter( out );
		
		if( format != null && "sparse".equalsIgnoreCase( format.trim() ) )
			return new SparseDataStreamWriter( out );
		
		return new SparseDataStreamWriter( out );
	}
	
	
	public static DataStream createDataInputStream( String format, InputStream in ) throws Exception {

		if( format != null && "csv".equalsIgnoreCase( format.trim() ) )
			return new CsvStream( in );
		
		if( format != null && "sparse".equalsIgnoreCase( format.trim() ) )
			return new SparseDataStream( in );
		
		return new SvmLightDataStream( in );
	}
}