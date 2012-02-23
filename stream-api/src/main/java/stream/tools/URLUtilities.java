package stream.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class URLUtilities
{

	public static String readContentOrEmpty( URL url ){
		try {
			return readContent( url );
		} catch (Exception e) {
			return "";
		}
	}


	public static String readContent( URL url ) throws Exception {
		if( url == null )
			return "";

		return readResponse( url.openStream() );
	}


	public static String readResponse( InputStream in ) throws Exception {
		StringBuffer s = new StringBuffer();
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = reader.readLine();
		while( line != null ){
			s.append( line + "\n" );
			line = reader.readLine();
		}
		reader.close();
		return s.toString();
	}
}
