package stream.io;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

/**
 * This class implements a simple reader to read data in the SVM light data
 * format. The data is read from a URL and parsed into a Data instance.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class SvmLightDataStream 
	extends AbstractDataStream 
{
	static Logger log = LoggerFactory.getLogger( SvmLightDataStream.class );
	long lineNumber = 0;
	
	
	public SvmLightDataStream( String url ) throws Exception {
		this( new URL( url ) );
	}
	

	public SvmLightDataStream(URL url) throws Exception {
		super(url);
		initReader();
	}
	

	/**
	 * @see stream.io.AbstractDataStream#readHeader()
	 */
	@Override
	public void readHeader() throws Exception {
	}


	/**
	 * @see stream.io.AbstractDataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data item) throws Exception {

		if( reader == null )
			initReader();
		
		String line = reader.readLine();
		if( line == null )
			return null;
		
		log.debug( "line[{}]: {}", lineNumber, line );
		lineNumber++;
		return parseLine( item, line );
	}

	/**
	 * This method parses a single line into a data item. The line is expected to
	 * match the format of the SVMlight data format.
	 * 
	 * @param item
	 * @param line
	 * @return
	 * @throws Exception
	 */
	public static Data parseLine( Data item, String line ) throws Exception {

		int info = line.indexOf( "#" );
		if( info > 0 )
			line = line.substring( 0, info );
		
		String[] token = line.split( "\\s+" );
		item.put( "@label", new Double( token[0] ) );

		for( int i = 1; i < token.length; i++ ){
			
			String[] iv = token[i].split( ":" );
			if( iv.length != 2 ){
				log.error( "Failed to split token '{}' in line: ", token[i], line );
				return null;
			} else {
				item.put( iv[0], new Double( iv[1]) );
			}
		}
		return item;
	}
}
