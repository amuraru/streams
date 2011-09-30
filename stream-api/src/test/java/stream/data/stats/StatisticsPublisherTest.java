/**
 * 
 */
package stream.data.stats;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class StatisticsPublisherTest {

	static Logger log = LoggerFactory.getLogger( StatisticsPublisherTest.class );

	public static void main( String[] args ) {
		try {
			String url = "http://admin:admin@kirmes.cs.uni-dortmund.de/results/stats/SGD_T1000_M2";
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
