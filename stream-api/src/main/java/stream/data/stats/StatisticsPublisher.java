/**
 * 
 */
package stream.data.stats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class StatisticsPublisher implements StatisticsListener {

	static Logger log = LoggerFactory.getLogger( StatisticsPublisher.class );
	private static String login = null;
	private static String password = null;
	
	URL resultUrl;
	
	static {
		
		try {
			Properties p = new Properties();
			p.load( new FileInputStream( new File( System.getProperty( "user.home" ) + "/.scinotes.properties" ) ) );
			if( p.getProperty( "scinotes.username" ) != null )
				login = p.getProperty( "scinotes.username" );
			
			if( p.getProperty( "scinotes.password" ) != null )
				password = p.getProperty( "scinotes.password" );
			
		} catch (Exception e) {
			
		}
		
	}
	
	public static void setAuthentication( String user, String pass ){
		login = user;
		password = pass;
	}

	public StatisticsPublisher( URL url ){
		resultUrl = url;
	}

	/**
	 * @see stream.data.stats.StatisticsListener#dataArrived(stream.data.stats.Statistics)
	 */
	@Override
	public void dataArrived(Statistics stats) {
		try {
			log.debug( "Processing incoming statistics {}", stats );
			publish( resultUrl, stats );
		} catch (Exception e) {
			log.error( "Failed to send statistics: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}
	}


	public static void publish( URL url, Statistics stats ) throws Exception {
		String userInfo = url.getUserInfo();
		
		if( userInfo == null && login != null && password != null )
			userInfo = login + ":" + password;
		
		String urlString = url.toExternalForm();
		if( userInfo != null )
			urlString = urlString.replace( userInfo, "****:****" );
		
		log.info( "Publishing statistics to URL {}", urlString );
		log.debug( "User info is: {}", urlString );

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

		
		try {
			File spoolDir = new File( System.getProperty( "user.home" ) + File.separator + ".scinotes" );
			if( System.getProperty( "scinotes.spool.directory" ) != null )
				spoolDir = new File( System.getProperty( "scinotes.spool.directory" ) );
			
			if( ! spoolDir.isDirectory() ){
				log.debug( "Creating spool directory {}", spoolDir );
				spoolDir.mkdirs();
			}
			
			String path = url.getPath();
			if( path.equals( "" ) || "/".equals( path ) ){
				path = "_unnamed.dat";
			}
			
			File spoolFile = new File( spoolDir.getAbsolutePath() + File.separator + path );
			if( spoolFile.getParentFile() != null && !spoolFile.getParentFile().isDirectory() ){
				log.debug( "Creating local spool directory {}", spoolFile.getParentFile() );
				spoolFile.getParentFile().mkdirs();
			}
			
			log.debug( "Writing to local spool file {}", spoolFile.getAbsolutePath() );
			FileOutputStream fout = new FileOutputStream( spoolFile, true );
			StatisticsStreamWriter w = new StatisticsStreamWriter( fout );
			w.dataArrived( stats );
			fout.flush();
			fout.close();			
		} catch (Exception e) {
			log.error( "Failed to write statistics to local spool file: {}", e.getMessage() );
			if( log.isTraceEnabled() )
				e.printStackTrace();
		}
		
		OutputStream out = con.getOutputStream();
		StatisticsStreamWriter writer = new StatisticsStreamWriter( out, ";" );
		writer.dataArrived( stats );
		out.flush();
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
		String line = reader.readLine();
		while( line != null ){
			log.info( "response: {}", line );
			line = reader.readLine();
		}
		reader.close();
	}
}