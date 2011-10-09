/**
 * 
 */
package net.scinotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;

/**
 * @author chris
 *
 */
public final class SciNotesService {

	static Logger log = LoggerFactory.getLogger( SciNotesService.class );
	
	final static SciNotesService globalInstance = new SciNotesService( "https://www.scinotes.net" );
	
	final Properties p = new Properties();
	URL remoteUrl;
	URL resultUrl;
	String basicAuth = null;

	protected SciNotesService() throws Exception {
		this( new URL( "https://www.scinotes.net" ) );
	}

	public SciNotesService( String url ){
		try {
			remoteUrl = new URL( url );
		} catch (Exception e) {
			remoteUrl = null;
			e.printStackTrace();
		}
		init();
	}

	private SciNotesService( URL url ){
		remoteUrl = url;
		init();
	}


	private void init(){
		p.put( "scinotes.user", System.getProperty( "user.name" ) );

		File userProps = null;
		try {
			userProps = new File( System.getProperty( "user.home" ) + File.separator + ".SciNotes.properties" );
			if( userProps.canRead() ){
				log.info( "Reading SciNotes properties from {}", userProps );
				p.load( new FileReader( userProps ) );
			}
		} catch (Exception e) {
			log.error( "Failed to read SciNotes properties from {}: {}", userProps, e.getMessage() );
			if( log.isTraceEnabled() )
				e.printStackTrace();
		}

		p.putAll( System.getProperties() );

		if( p.getProperty( "scinotes.user" ) != null && p.getProperty( "scinotes.password" ) != null ){
			String user = p.getProperty( "scinotes.user" );
			String pass = p.getProperty( "scinotes.password" );
			basicAuth = "Basic " + new String( Base64.encodeBase64( (user + ":" + pass ).getBytes() ) );
		}
	}


	public static SciNotesService getInstance(){
		return null;
	}


	public void log( String key, String msg ){
		log( 0, key, msg );
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

	
	public void sendResults( String key, Statistics statistics ){
		

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
