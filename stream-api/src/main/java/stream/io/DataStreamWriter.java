/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;


/**
 * <p>
 * This class implements a listener which will write all its incoming
 * data into a one-by-one line output.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class DataStreamWriter 
	implements DataStreamListener 
{
	static Logger log = LoggerFactory.getLogger( DataStreamWriter.class );
	PrintStream p;
	String separator = " ";
	boolean headerWritten = false;
	String filter = ".*";
	DataFilter dataFilter = null;
	List<String> headers = new LinkedList<String>();
	boolean closed = false;
	
	/**
	 * Create a new DataStreamWriter which writes all data to the
	 * given file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public DataStreamWriter( File file ) throws IOException {
		this( new FileOutputStream(file) );
	}
	
	public DataStreamWriter( File file, String separator) throws IOException {
		this( file );
		this.separator = separator;
	}
	
	
	/**
	 * 
	 * 
	 * @param out
	 */
	public DataStreamWriter( OutputStream out ){
		this( out, ";" );
	}

	public DataStreamWriter( OutputStream out, String separator ){
		p = new PrintStream( out );
		this.separator = separator;
	}

	
	public void setAttributeFilter( String filter ){
		this.filter = filter;
	}
	
	public void setDataFilter( DataFilter filter ){
		this.dataFilter = filter;
	}
	
	public List<String> getHeaderNames(){
		return headers;
	}
	
	
	/**
	 * @see stream.io.DataStreamListener#dataArrived(java.util.Map)
	 */
	@Override
	public void dataArrived(Data datum) {
		
		if( closed ){
			log.error( "DataStreamWriter is closed! Not writing any more data items!" );
			return;
		}
		
		if( dataFilter != null && !dataFilter.matches( datum ) )
			return;
		
		// write the keys of the very first datum ONCE (attribute names)
		//
		if( ! headerWritten ){
			p.print( "#" );
			Iterator<String> it = datum.keySet().iterator();
			while( it.hasNext() ){
				String name = it.next();
				headers.add( name );
				p.print( name );
				if( it.hasNext() )
					p.print( separator );
			}
			p.println();
			headerWritten = true;
		}

		// write the datum elements (attribute values)
		// 
		Iterator<String> it = datum.keySet().iterator();
		while(it.hasNext() ){
			String name = it.next();
			p.print( datum.get( name ) );
			if( it.hasNext() )
				p.print( separator );
		}
		p.println();
	}
	
	
	public void close(){
		p.flush();
		p.close();
		closed = true;
	}
}