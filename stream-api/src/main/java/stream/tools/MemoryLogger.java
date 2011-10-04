/**
 * 
 */
package stream.tools;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;
import stream.data.stats.StatisticsPublisher;
import stream.util.ExperimentLog;

/**
 * @author chris
 *
 */
public class MemoryLogger extends Thread {

	static Logger log = LoggerFactory.getLogger( MemoryLogger.class );

	public MemoryLogger(){
	}


	public void run(){
		while( true ){
			Long mem0 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			ExperimentLog.log( ExperimentLog.STATUS, "Current memory usage is {} bytes", mem0 );

			try {
				if( System.getProperty( "memory.stats.url" ) != null ){
					URL url = new URL( System.getProperty( "memory.stats.url" ) );
					Statistics stats = new Statistics();
					stats.put( "memory.usage", mem0.doubleValue() );
					StatisticsPublisher.publish( url, stats);
				}
			} catch (Exception e) {
				if( log.isTraceEnabled() )
					e.printStackTrace();
			}
			
			try {
				Thread.sleep( 5 * 1000L );
			} catch (Exception e) {
			}
		}
	}
}