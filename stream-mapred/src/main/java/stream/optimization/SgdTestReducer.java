/**
 * 
 */
package stream.optimization;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;
import stream.data.stats.StatisticsPublisher;
import stream.reducer.Sum;

/**
 * @author chris
 *
 */
public class SgdTestReducer extends Sum {

	static Logger log = LoggerFactory.getLogger( SgdTestReducer.class );

	
	public void init() throws Exception {
		super.init();
		
		for( Object o : System.getProperties().keySet() ){
			try {
				
				if( o.toString().startsWith( "experiment.args." ) ){
					String key = o.toString().substring( "experiment.args.".length() );
					stats.put( key, new Double( System.getProperty( o.toString() ) ) );
				}
				
				if( o.toString().startsWith( "stats." ) ){
					String key = o.toString().substring( "stats.".length() );
					stats.put( key, new Double( System.getProperty( o.toString() ) ) );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	public void finish() throws Exception {
		super.finish();
		publish( stats );
	}
	
	
	public void publish( Statistics stats ){
		try {
			String url = resultUrl;
			if( url == null ){
				log.debug( "Checking for system property 'experiment.result.url'" );
				url = System.getProperty( "experiment.result.url" );
			}
			log.debug( "result url is: '{}'", url );
			if( url != null ){
				log.trace( "Publishing results at {}", url );
				stats.put( "@timestamp", new Double( System.currentTimeMillis() ) );
				StatisticsPublisher.publish( new URL(url), stats );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}