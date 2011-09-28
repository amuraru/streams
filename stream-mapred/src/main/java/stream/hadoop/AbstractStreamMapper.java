package stream.hadoop;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;
import stream.io.SvmLightDataStream;


/**
 * This class implements a simple map job, that reads data from standard
 * input and processes the data in one block (list of data items). The
 * data is assumed to be in SVM light format (sparse).
 * 
 * @author Christian Bockermann
 *
 */
public abstract class AbstractStreamMapper 
	implements StreamMapper 
{
	/* The logger for this class */
	static Logger log = LoggerFactory.getLogger( AbstractStreamMapper.class );

	/* The output writer */
	private PrintStream outputStream;
	
	
	public DataStream createDataStream( InputStream in ) throws Exception {
		return new SvmLightDataStream( in );
	}

	
	public List<Data> readBlock( InputStream in ){

		List<Data> block = new ArrayList<Data>();
		try {
			DataStream stream = createDataStream( in );
			Data item = stream.readNext();

			log.debug( "Reading input data" );
			while( item != null ){
				block.add( item );
				try {
					item = stream.readNext();
				} catch (Exception e) {
					log.error( "Failed to read item: {}", e.getMessage() );
					item = null;
				}
			}
		} catch (Exception e) {
			log.error( "Failed reading input-block: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}
		return block;
	}
	
	
	public void run( InputStream in, OutputStream out ) throws Exception {
		outputStream = new PrintStream( out );
		this.init();
		
		DataStream stream = this.createDataStream( in );
		Data item = stream.readNext();
		while( item != null ){
			process( item );
			item = stream.readNext();
		}
		
		finish();
	}
	
	public final PrintStream getWriter(){
		return outputStream;
	}
}