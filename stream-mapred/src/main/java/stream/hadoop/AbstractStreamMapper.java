package stream.hadoop;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
	implements HadoopStreamMapper 
{
	/* The logger for this class */
	static Logger log = LoggerFactory.getLogger( AbstractStreamMapper.class );

	/* The output writer */
	private PrintWriter writer;
	
	
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


	/**
	 * This method is called before the data is processed. This is intended to be
	 * used for initialization.
	 */
	@Override
	public void init(){
	}
	

	/**
	 * This is the main work method. It can be used to iterate over the data block
	 * multiple times. 
	 */
	@Override
	public void process(List<Data> items) {
	}

	/**
	 * This method is called after the processing is completed. Usually this is useful
	 * for cleaning up any temporal data, etc.
	 */
	@Override
	public void finish(){
	}
	
	
	private final void run( List<Data> block ){
		init();
		process( block );
		getWriter().flush();
		finish();
		getWriter().flush();
		getWriter().close();
	}
	
	
	public final void run( InputStream in, OutputStream out ){
		List<Data> block = readBlock( in );
		writer = new PrintWriter( out );
		run( block );
	}
	
	public final PrintWriter getWriter(){
		return writer;
	}
}