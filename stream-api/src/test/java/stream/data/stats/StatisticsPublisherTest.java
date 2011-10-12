/**
 * 
 */
package stream.data.stats;

import java.net.URL;

import net.scinotes.gui.AuthenticationDialog;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class StatisticsPublisherTest {

	static Logger log = LoggerFactory.getLogger( StatisticsPublisherTest.class );

	// a test dummy
	@Test
	public void test(){
	}

	public static void main( String[] args ) {
		try {
			
			String[] tok = AuthenticationDialog.authenticate();
			if( tok != null ){
				StatisticsPublisher.setAuthentication( tok[0], tok[1] );
			}
			
			String url = "http://localhost:8080/results/stats/test";
			log.info( "Publishing statistics at {}", url );
			Statistics st = new Statistics();
			st.add( "Test", 1.0d );
			st.add( "ABC", 2.0d );

			StatisticsPublisher.publish( new URL( url ), st );
		} catch (Exception e) {
			log.error( "Failed to publish: {}", e.getMessage() );
			e.printStackTrace();
		}
	}
}
