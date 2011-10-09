/**
 * 
 */
package net.scinotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsStreamWriter;

/**
 * <p>
 * This is a simple API interface implementation that provides access to the
 * most important functionality with the SciNotes system.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class SciNotesAPI {
	
	static Logger log = LoggerFactory.getLogger( SciNotesAPI.class );
	protected final static SciNotesService sciNotes = new SciNotesService( "https://www.scinotes.net" );
	
	public final static String SCINOTE_DATA_HEADER = "X-SciNotes-Data";
	public final static String SCINOTES_STATISTICS = "Statistics";
	
	public final static Map<String,StatisticsStreamWriter> localWriters = new HashMap<String,StatisticsStreamWriter>();
	private final static Properties p = new Properties();
	
	
	static {
		log.info( "Initializing SciNotes API" );
		init();
		log.info( "Using SciNotes URL: {}", getBaseURL() );
		log.info( "  SciNotes Username is: {}", System.getProperty( "scinotes.user" ) );
	}
	
	
	
	
	public static String getBaseURL(){
		return "http://localhost:8080";
	}
	
	


	private static void init(){
		p.put( "scinotes.user", System.getProperty( "user.name" ) );

		File userProps = null;
		try {
			userProps = new File( System.getProperty( "user.home" ) + File.separator + ".SciNotes.properties" );
			log.debug( "Checking properties file  {}", userProps );
			
			if( userProps.canRead() ){
				log.info( "Reading SciNotes properties from {}", userProps );
				p.load( new FileReader( userProps ) );
			}
		} catch (Exception e) {
			log.error( "Failed to read SciNotes properties from {}: {}", userProps, e.getMessage() );
			if( log.isTraceEnabled() )
				e.printStackTrace();
		}

		for( Object o : p.keySet() ){
			log.debug( "    {} = {}", o.toString(), p.getProperty( o.toString() ) );
		}

		/*
		if( p.getProperty( "scinotes.user" ) != null && p.getProperty( "scinotes.password" ) != null ){
			String user = p.getProperty( "scinotes.user" );
			String pass = p.getProperty( "scinotes.password" );
			//"Basic " + new String( Base64.encodeBase64( (user + ":" + pass ).getBytes() ) );
		}
		 */
	}


	public static synchronized StatisticsStreamWriter getLocalWriter( String key ) throws Exception {
		log.debug( "Looking up local statistics-writer for key '{}'", key );
		if( localWriters.get( key ) != null ){
			log.debug( "Found existing local statistics writer!" );
			return localWriters.get( key );
		}
		
		File file = new File( key + ".stats" );
		log.debug( "Appending to local statistics writer {}", file );
		StatisticsStreamWriter writer = new StatisticsStreamWriter( new FileOutputStream( key + ".stats", true ) );
		localWriters.put( key, writer );
		return writer;
	}
	
	
	public final static boolean send( String name, Statistics stats ) throws Exception {
		List<Statistics> statistics = new ArrayList<Statistics>();
		statistics.add( stats );
		return 1 == send( name, statistics );
	}
	
	
	public static int send( String name, Collection<Statistics> stats ) throws Exception {
		log.debug( "Need to write {} statistic elements", stats.size() );
		StatisticsStreamWriter local = null;
		try {
			local = getLocalWriter( name );
		} catch (Exception e) {
			throw new Exception( "Failed to create local writer: " + e.getMessage() );
		}
		
		for( Statistics st : stats ){
			local.dataArrived(st);
		}
		
		log.debug( "Statistics written to local writer..." );
		
		URL url = new URL( getBaseURL() + "/results/" + name );
		log.debug( "Sending {} statistic items to {}", stats.size(), url );
		
		String basicAuth = createBasicAuthentication( url );
		log.debug( "Authentication: {}", basicAuth );

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod( "PUT" );
		con.setDoInput( true );
		con.setDoOutput( true );

		if( basicAuth != null )
			con.setRequestProperty( "authorization", basicAuth );

		con.setAllowUserInteraction( false );
		con.connect();

		OutputStream out = con.getOutputStream();
		StatisticsStreamWriter writer = new StatisticsStreamWriter( out, ";" );
		for( Statistics st : stats ){
			log.debug( "Sending statistics {}", st );
			writer.dataArrived( st );
		}
		out.flush();
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
		StringBuffer s = new StringBuffer();
		String line = reader.readLine();
		while( line != null ){
			s.append( line.trim() + "\n" );
			log.debug( "response: {}", line );
			line = reader.readLine();
		}
		reader.close();
		try {
			return new Integer( s.toString().trim() );
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	public static boolean send( Data data ) throws Exception {
		return false;
	}
	
	
	public static String createBasicAuthentication( URL url ){
		String userInfo = url.getUserInfo();
		if( url.getUserInfo() == null ){
			if( p.getProperty( "scinotes.user" ) != null && p.getProperty( "scinotes.password" ) != null ){
				userInfo = p.getProperty( "scinotes.user" ) + ":" + p.getProperty( "scinotes.password" );
			} else
				return null;
		}
		String basicAuth = "Basic " + new String( Base64.encodeBase64( userInfo.getBytes() ) );
		log.debug( "Basic authentication is {}", basicAuth );
		return basicAuth;
	}
}