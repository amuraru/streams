package stream.optimization;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.mapred.StreamReducer;

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
	extends StreamReducer
{
	static Logger log = LoggerFactory.getLogger( SgdReducer.class );
	
	Double testCount = 0.0d;
	Double testError = 0.0d;
	Data result;
	

	public void init() throws Exception {
		result = new DataImpl();
	}

	
	public Data process( Data item ){

		try {
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

			testCount += 1.0d;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}


	public void finish() throws Exception {
		write( result );
	}


	public static void main( String[] args ) throws Exception {
		SgdReducer reducer = new SgdReducer();

		InputStream in = System.in;
		OutputStream out = System.out;
		reducer.init( in, out );
		reducer.reduce();
	}
}