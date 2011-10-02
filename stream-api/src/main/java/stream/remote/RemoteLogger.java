/**
 * 
 */
package stream.remote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class RemoteLogger {
	
	static Logger log = LoggerFactory.getLogger( RemoteLogger.class );
	URL remoteUrl;

	
	public RemoteLogger( URL url ){
		remoteUrl = url;
	}
	
	public void log( Integer level, String key, String msg ){
		try {
			log.debug( "Sending log-message to url {}", remoteUrl );
			send( remoteUrl, level, key, msg );
		} catch (Exception e) {
			log.error( "Failed to send log-message to remote URL: {}", e.getMessage() );
			log.error( "   remote-url is: {}", remoteUrl );
			log.error( "   log-message is: {}", msg );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}
	}
	

	public static void send( URL url, Integer level, String key, String msg ) throws Exception {
		
		if( url == null ){
			log.warn( "No remote logging-url specified! Not logging this message: {}", msg );
			return;
		}
		
		
		String userInfo = url.getUserInfo();
		
		String urlString = url.toExternalForm();
		if( userInfo != null )
			urlString = urlString.replace( userInfo, "****:****" );
		
		log.debug( "Publishing statistics to URL {}", urlString );
		log.trace( "User info is: {}", userInfo );

		URLConnection con = url.openConnection();
		con.setDoInput( true );
		con.setDoOutput( true );
		
		if( userInfo != null ){
			try {
				String cred = new String( Base64.encodeBase64( userInfo.getBytes() ) );
				con.setRequestProperty( "authorization", "Basic " + cred );
			} catch (Exception e) {
				log.error( "Failed to set authorization request property: {}", e.getMessage() );
				if( log.isDebugEnabled() )
					e.printStackTrace();
			}
		}
		
		con.setAllowUserInteraction( false );
		con.connect();

		OutputStream out = con.getOutputStream();
		PrintWriter writer = new PrintWriter( out );
		writer.print( "" + System.currentTimeMillis() );
		writer.print( ";" + level );
		writer.print( ";" + key );
		writer.println( ";" + msg.replaceAll( "\n", "\\n" ) );
		writer.flush();
		writer.close();
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
		String line = reader.readLine();
		while( line != null ){
			log.info( "response: {}", line );
			line = reader.readLine();
		}
		reader.close();
	}
}