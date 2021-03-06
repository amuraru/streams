/**
 * 
 */
package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;


/**
 * <p>
 * This class implements a listener which will write all its incoming
 * data into a one-by-one line output.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class DataStreamWriter 
	implements DataStreamListener, DataProcessor
{
	static Logger log = LoggerFactory.getLogger( DataStreamWriter.class );
	PrintStream p;
	String separator = " ";
	String lastHeader = null;
	boolean headerWritten = false;
	String filter = ".*";
	DataFilter dataFilter = null;
	List<String> headers = new LinkedList<String>();
	boolean closed = false;
	List<String> keys = null;
	String url;

	
	public DataStreamWriter(){
	}
	
	public DataStreamWriter( URL url ) throws Exception {
		this( new FileOutputStream( new File( url.toURI() ) ) );
	}
	
	
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
	
	
	public void setUrl( String url ){
		this.url = url;
		File file = new File( url );
		try {
			p = new PrintStream( new FileOutputStream( file ) );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getUrl(){
		return this.url;
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
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public void setKeys( String str ){
	    if( str == null )
	        keys = null;
	    else {
	        String[] ks = str.split( "," );
	        keys = new ArrayList<String>();
	        for( String k : ks )
	            keys.add( k );
	    }
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
		
		writeHeader( datum );

		// write the datum elements (attribute values)
		// 
		write( datum );
	}
	
	public void writeHeader( Data datum ){
		// write the keys of the very first datum ONCE (attribute names)
		// or if the number of keys has changed
		//
		String header = createHeader( datum );
		if( lastHeader != null && lastHeader.equals( header ) )
			return;
		
		if( lastHeader == null || !lastHeader.equals( header ) ){
			p.println( header );
			lastHeader = header;
			return;
		}
		
		
		if( ! headerWritten || ( keys == null && datum.keySet().size() > headers.size() ) ){
			p.print( "#" );
			Iterator<String> it = datum.keySet().iterator();
			if( keys != null )
			    it = keys.iterator();
			
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
	}
	
	public void write( Data datum ){

		// write the datum elements (attribute values)
		// 
		
		Iterator<String> it = null;
		if( keys != null )
		    it = keys.iterator();
		else
		    it = datum.keySet().iterator();
		
		while( it.hasNext() ){
			String name = it.next();
			String stringValue = "?";
			Serializable val = datum.get( name );
			
			if( val != null )
			    stringValue = val.toString().replaceAll( "\\n", "\\\\n" );
			else
			    stringValue = "null";
			
			p.print( stringValue );
			if( it.hasNext() )
				p.print( separator );
		}
		p.println();
	}
	
	
	protected String createHeader( Data item ){
		StringWriter s = new StringWriter();
		s.append( "#" );
		Iterator<String> it = item.keySet().iterator();
		
		if( keys != null )
			it = keys.iterator();
		
		while( it.hasNext() ){
			s.append( it.next() );
			if( it.hasNext() )
				s.append( separator );
		}
		return s.toString();
	}
	
	
	public void close(){
		p.flush();
		p.close();
		closed = true;
	}

	@Override
	public Data process(Data data) {
		dataArrived( data );
		return data;
	}
}