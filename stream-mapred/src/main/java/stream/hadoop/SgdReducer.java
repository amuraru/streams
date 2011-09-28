package stream.hadoop;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.io.SparseDataStream;
import stream.io.SparseDataStreamWriter;

/**
 * <p>
 * This class implements a simple reduction of several w_t vectors by just
 * computing the average vector. The input is expected to be in sparse format.
 * </p>
 * 
 * @author Christian Bockermann
 *
 */
public class SgdReducer 
extends AbstractStreamReducer<Data,Data>
{
	static Logger log = LoggerFactory.getLogger( SgdReducer.class );
	SparseDataStream stream;
	SparseDataStreamWriter writer;

	public void init( InputStream in, OutputStream out ){
		super.init( in, out);
		try {
			this.stream = new SparseDataStream( in );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.writer = new SparseDataStreamWriter( out );
	}


	public void reduce(){
		Data result = new DataImpl();
		double count = 0.0d;

		try {
			Data item = stream.readNext();
			while( item != null ){
				for( String key : item.keySet() ){
					Serializable val = item.get( key );
					if( val instanceof Double ){
						Double value = (Double) val;
						if( result.get( key ) == null ){
							result.put( key, value );
						} else {
							result.put( key, value + (Double) result.get( key ) );
						}
					} else {
						result.put( key, item.get( key ) );
					}
				}
				
				count += 1.0d;
				
				item = stream.readNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for( String key : result.keySet() ){
			Serializable val = result.get( key );
			if( val instanceof Double ){
				Double value = (Double) val;
				result.put( key, value / count );
			}
		}

		write( result );
	}


	/**
	 * This method simply reads the next line from the input reader and tries to
	 * create a sparse-vector from that line. If no more lines can be read, then
	 * this method simply returns <code>NULL</code>.
	 * 
	 * @see stream.hadoop.StreamReducer#read()
	 */
	@Override
	public Data read() {

		try {
			Data item = stream.readNext();
			return item;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * @see stream.hadoop.StreamReducer#write(java.lang.Object)
	 */
	@Override
	public void write(Data out) {
		if( out != null ){
			this.writer.dataArrived( out );
		}
	}


	public static void main( String[] args ) throws Exception {
		SgdReducer reducer = new SgdReducer();

		InputStream in = System.in;
		//in = SgdReducer.class.getResourceAsStream( "/test-map.out" );
		OutputStream out = System.out;
		reducer.reduce( in, out );
	}
}