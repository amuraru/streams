/**
 * 
 */
package stream.io;

import java.net.URL;
import java.util.LinkedList;

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
		
		String[] tok = line.split(  splitExpression  );
		for( int i = 0; i < tok.length; i++ ){
			tok[i] = removeQuotes(tok[i]);
			attributes.put( tok[i], Double.class );
		}
		
		String data = reader.readLine();
		while( data.startsWith( "#" ) ){
			data = reader.readLine();
		}
		
		buffer.add( data );
		String dt[] = data.split( splitExpression );
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
		while( line != null && (line.trim().isEmpty() || line.startsWith( "#" ) ) ){
			if( line.startsWith( "#" ) ){
				String dt[] = line.split( splitExpression );
				for( int i = 0; i < dt.length; i++ ){
					if( i < dt.length ){
						if( dt[i].matches( "\\d*\\.\\d*" ) )
							attributes.put( dt[i], Double.class );
						else
							attributes.put( dt[i], String.class );
					}
				}
			}
			
			line = reader.readLine();
		}
		

		if( line != null && ! line.trim().equals( "" ) ){
			String[] tok = line.split( splitExpression ); // QuotedStringTokenizer.splitRespectQuotes( line, ';'); //line.split( "(;|,)" );
			int i = 0;
			for( String name : attributes.keySet() ){
				if( i < tok.length ){
					if( Double.class.equals( attributes.get( name ) ) ){
						datum.put( name, new Double( removeQuotes( tok[i] ) ) );
					} else
						datum.put( name, removeQuotes( tok[i] ) );
					i++;
				} else
					break;
			}
		} else
			return null;
		
		return preprocess( datum );
	}
	
	public String readLine() throws Exception {
		
		if( reader == null )
			initReader();
		
		if( buffer != null && ! buffer.isEmpty() )
			return buffer.removeFirst();
		return reader.readLine();
	}
	
	
	protected Data preprocess( Data datum ) throws Exception {
		if( datum == null )
			return null;
		
		return datum;
	}
}