/**
 * 
 */
package stream.data.stats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import stream.io.DataFilter;


/**
 * <p>
 * This class implements a listener which will write all its incoming
 * data into a one-by-one line output.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class StatisticsStreamWriter 
	implements StatisticsListener 
{
	PrintStream p;
	String separator = " ";
	boolean headerWritten = false;
	String filter = ".*";
	DataFilter dataFilter = null;
	List<String> headers = new LinkedList<String>();
	
	/**
	 * Create a new DataStreamWriter which writes all data to the
	 * given file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public StatisticsStreamWriter( File file ) throws IOException {
		this( new FileOutputStream(file) );
	}
	
	public StatisticsStreamWriter( File file, String separator) throws IOException {
		this( file );
		this.separator = separator;
	}
	
	
	/**
	 * 
	 * 
	 * @param out
	 */
	public StatisticsStreamWriter( OutputStream out ){
		p = new PrintStream( out );
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
	public void dataArrived(Statistics datum) {

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
}