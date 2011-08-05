/**
 * 
 */
package stream.io;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import stream.data.Data;
import stream.data.DataImpl;

/**
 * @author chris
 *
 */
public class CsvStream
	extends AbstractDataStream
{
	String splitExpression = "(;|,)";
	LinkedList<String> buffer;
	
	/**
	 * @param url
	 * @throws Exception
	 */
	public CsvStream(URL url) throws Exception {
		super(url);
	}
	
	
	public CsvStream(URL url, String splitExp) throws Exception {
		super( url );
		this.url = url;
		this.splitExpression = splitExp;
		this.initReader();
	}

	
	public String removeQuotes( String str ){
		String s = str;
		if( s.startsWith( "\"" ) )
			s = s.substring( 1 );
		
		if( s.endsWith( "\"" ) )
			s = s.substring( 0, s.length() - 1 );
			
		return s;
	}


	public void readHeader() throws Exception {
		if( buffer == null )
			buffer = new LinkedList<String>();
		
		String line = reader.readLine();

		while( line.startsWith( "#" ) )
			line = line.substring( 1 );
		
		String[] tok = line.split(  "(;|,)"  );
		for( int i = 0; i < tok.length; i++ ){
			tok[i] = removeQuotes(tok[i]);
			attributes.put( tok[i], Double.class );
		}
		
		String data = reader.readLine();
		while( data.startsWith( "#" ) ){
			data = reader.readLine();
		}
		
		buffer.add( data );
		String dt[] = data.split(  "(;|,)"  );
		for( int i = 0; i < tok.length; i++ ){
			if( i < dt.length ){
				if( dt[i].matches( "\\d*\\.\\d*" ) )
					attributes.put( tok[i], Double.class );
				else
					attributes.put( tok[i], String.class );
			}
		}
	}


	/**
	 * @see stream.io.DataStream#readNext()
	 */
	public Data readNext( Data datum ) throws Exception {
		if( datum == null )
			datum = new DataImpl();
		else
			datum.clear();

		String line = readLine();
		while( line != null && (line.trim().isEmpty() || line.startsWith( "#" ) ) )
			line = reader.readLine();

		if( line != null && ! line.trim().equals( "" ) ){
			List<String> tok = QuotedStringTokenizer.splitRespectQuotes( line, ';'); //line.split( "(;|,)" );
			int i = 0;
			for( String name : attributes.keySet() ){
				if( i < tok.size() ){
					if( Double.class.equals( attributes.get( name ) ) ){
						datum.put( name, new Double( removeQuotes(tok.get(i)) ) );
					} else
						datum.put( name, removeQuotes(tok.get(i)) );
					i++;
				} else
					break;
			}
		} else
			return null;
		return datum;
	}
	
	public String readLine() throws Exception {
		if( buffer != null && ! buffer.isEmpty() )
			return buffer.removeFirst();
		return reader.readLine();
	}
}