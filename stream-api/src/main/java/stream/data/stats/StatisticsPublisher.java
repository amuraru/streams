/**
 * 
 */
package stream.data.stats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class StatisticsPublisher implements StatisticsListener {

	static Logger log = LoggerFactory.getLogger( StatisticsPublisher.class );
	URL resultUrl;

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
		
		String urlString = url.toExternalForm();
		if( userInfo != null )
			urlString = urlString.replace( userInfo, "****:****" );
		
		log.info( "Publishing statistics to URL {}", urlString );
		log.debug( "User info is: {}", userInfo );

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
		StatisticsStreamWriter writer = new StatisticsStreamWriter( out );
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