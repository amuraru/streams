/**
 * 
 */
package stream.io;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.util.Description;

/**
 * <p>
 * This class implements a streaming source providing information from an
 * ARFF file form. 
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
@Description( group="Data Stream.Sources" )
public class ArffStream 
	extends AbstractDataStream
{
	static Logger log = LoggerFactory.getLogger( ArffStream.class );
	
	/**
	 * @param url
	 * @throws Exception
	 */
	public ArffStream(URL url) throws Exception {
		super(url);
	}
	


	public void readHeader() throws Exception {
		String line = reader.readLine();

		while( line != null && !line.startsWith( "@data" ) ){
			if( line.startsWith( "@attribute" ) ){
				String[] tok = line.split( "\\s" );
				Class<?> clazz = Object.class;
				if( "numeric".equalsIgnoreCase( tok[2].trim() ) )
					clazz = Double.class;
				
				String app = "";
				int i = 0;
				while( attributes.containsKey( tok[1] + app ) )
					app = "_" + (i++);
				
				attributes.put( tok[1] + app, clazz );
			}
			line = reader.readLine();
		}
		
		log.info( "Attributes of Arff-Stream: {}", attributes );
	}


	/**
	 * @see stream.io.AbstractDataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readItem(Data datum) throws Exception {
		if( datum == null )
			datum = new DataImpl();
		else
			datum.clear();
		
		String line = reader.readLine();
		while( line != null && line.trim().isEmpty() )
			line = reader.readLine();

		if( line != null && ! line.trim().equals( "" ) ){
			String[] tok = line.split( "," );
			int i = 0;
			for( String name : attributes.keySet() ){
				if( i < tok.length ){
					if( Double.class.equals( attributes.get( name ) ) ){
						datum.put( name, new Double( tok[i] ) );
					} else
						datum.put( name, tok[i] );
					i++;
				} else
					break;
			}
		}
		return datum;
	}
	
	

	
	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			log.error( "Failed to properly close reader: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}
	}
}