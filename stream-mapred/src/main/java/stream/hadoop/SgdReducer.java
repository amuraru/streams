package stream.hadoop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.vector.InputVector;
import stream.data.vector.Vector;
import stream.io.SvmLightDataStream;

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
extends AbstractStreamReducer<Vector,Vector>
{
	static Logger log = LoggerFactory.getLogger( SgdReducer.class );
	BufferedReader reader;

	public void init( InputStream in, OutputStream out ){
		super.init( in, out);
		reader = new BufferedReader( new InputStreamReader( in ) );
	}


	public void reduce(){
		Vector avg = new Vector();
		double count = 0.0d;
		Vector input = read();
		while( input != null ){
			avg.add( input );
			count += 1.0d;
			input = read();
		}

		if( count > 0 )
			avg.scale( 1 / count );

		write( avg );
	}


	/**
	 * @see stream.hadoop.HadoopStreamReducer#read()
	 */
	@Override
	public Vector read() {

		try {
			String line = reader.readLine();
			log.debug( "line: {}", line );
			if( line == null )
				return null;
			String[] tok = line.split( "\\t", 2 );
			if( tok.length > 1 )
				line = "1.0 " + tok[1];
			InputVector vec = SvmLightDataStream.readSparseVector( line );
			log.debug( "Read vector: {}", vec );
			return vec;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * @see stream.hadoop.HadoopStreamReducer#write(java.lang.Object)
	 */
	@Override
	public void write(Vector out) {
		if( out != null ){
			getWriter().print( "avg_w\t" );
			Map<Integer,Double> pairs = out.getPairs();
			TreeSet<Integer> keys = new TreeSet<Integer>( pairs.keySet() );
			for( Integer key : keys ){
				getWriter().print( " " );
				getWriter().print( key );
				getWriter().print( ":" );
				getWriter().print( pairs.get( key ) );
			}
			getWriter().println();
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