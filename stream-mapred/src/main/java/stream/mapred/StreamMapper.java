package stream.mapred;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.CsvStream;
import stream.io.DataStream;


/**
 * This class implements a simple map job, that reads data from standard
 * input and processes the data in one block (list of data items). The
 * data is assumed to be in SVM light format (sparse).
 * 
 * @author Christian Bockermann
 *
 */
public abstract class StreamMapper
	extends AbstractDataProcessor
	implements Mapper 
{
	/* The logger for this class */
	static Logger log = LoggerFactory.getLogger( StreamMapper.class );


	/**
	 * @see stream.mapred.Mapper#init()
	 */
	@Override
	public void init() throws Exception {
	}

	
	/**
	 * @see stream.mapred.Mapper#finish()
	 */
	@Override
	public void finish() throws Exception {
	}

	
	
	/**
	 * @see stream.mapred.AbstractDataProcessor#createDataInputStream(java.io.InputStream)
	 */
	@Override
	public DataStream createDataInputStream(InputStream in) throws Exception {
		if( "csv".equalsIgnoreCase( System.getProperty( "mapper.input.format" ) ) )
			return new CsvStream( in );
		
		return super.createDataInputStream( in );
	}

	
	public final void run( InputStream in, OutputStream out ) throws Exception {
		this.init( in, out );
		this.init();
		
		Data item = read();
		while( item != null ){
			process( item );
			item = read();
		}
		
		finish();
	}
}