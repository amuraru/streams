/**
 * 
 */
package net.scinotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;

/**
 * @author chris
 *
 */
public final class SciNotesClient {

	static Logger log = LoggerFactory.getLogger( SciNotesClient.class );
	
	final Properties p = new Properties();
	URL remoteUrl;
	URL resultUrl;
	String basicAuth = null;
	String username;
	String password;
	String token;

	protected SciNotesClient() throws Exception {
		this( new URL( "https://www.scinotes.net" ) );
	}

	private SciNotesClient( URL url ){
		remoteUrl = url;
		init();
	}
	
	public SciNotesClient( String url, String user, String pass ) throws Exception {
		remoteUrl = new URL( url );
		this.username = user;
		this.password = pass;
		token = this.authenticate( username, password );
		if( token == null )
			throw new Exception( "Authentication failed!" );
		log.debug( "Authentication successful, remembering credentials..." );
		basicAuth = "Basic " + new String( Base64.encodeBase64( ( username + ":" + password ).getBytes() ) );
	}



	public String authenticate( String login, String pass ){
		try {
			String basicAuth = "Basic " + new String( Base64.encodeBase64( ( login + ":" + pass ).getBytes() ) );

			URL url = new URL( remoteUrl + "/authenticate?username=" + URLEncoder.encode( login, "UTF-8" ) + "&password=" + URLEncoder.encode( pass, "UTF-8" ) );
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput( false );
			con.setDoInput( true );
			con.setRequestProperty( "authorization", basicAuth );
			String result = read( con.getInputStream() );
			log.debug( "Authentication result is: {}", result );
			
			if( result == null || "".equals( result.trim() ) )
				return null;

			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
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
			username = p.getProperty( "scinotes.user" );
			password = p.getProperty( "scinotes.password" );
			basicAuth = "Basic " + new String( Base64.encodeBase64( (username + ":" + password).getBytes() ) );
		}
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
	

	public void send( URL url, Integer level, String key, String msg ) throws Exception {

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
				basicAuth = new String( Base64.encodeBase64( userInfo.getBytes() ) );
			} catch (Exception e) {
				log.error( "Failed to set authorization request property: {}", e.getMessage() );
				if( log.isDebugEnabled() )
					e.printStackTrace();
			}
		}
		
		if( basicAuth != null ){
			con.setRequestProperty( "authorization", basicAuth );
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

		read( con.getInputStream() );
	}
	
	
	


	protected static String read( InputStream in ){
		StringBuffer s = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
			String line = reader.readLine();
			while( line != null ){
				s.append( line.trim() + "\n" );
				log.debug( "response: {}", line );
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s.toString();
	}
}
