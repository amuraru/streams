/**
 * 
 */
package stream.data.stats;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class WindowedStatisticsListener implements StatisticsListener {

	static Logger log = LoggerFactory.getLogger( WindowedStatisticsListener.class );
	Integer windowSize = 100;
	Boolean average = false;
	StatisticsListener inner;
	
	Statistics current = new Statistics();
	List<Statistics> window = new ArrayList<Statistics>( windowSize );
	
	public WindowedStatisticsListener( StatisticsListener l ){
		this.inner = l;
	}
	
	public WindowedStatisticsListener( StatisticsListener l, int size, boolean avg ){
		this( l );
		this.windowSize = Math.max( 1, size);
		this.average = avg;
	}
	
	
	/**
	 * @return the windowSize
	 */
	public Integer getWindowSize() {
		return windowSize;
	}

	/**
	 * @param windowSize the windowSize to set
	 */
	public void setWindowSize(Integer windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * @return the average
	 */
	public Boolean getAverage() {
		return average;
	}

	/**
	 * @param average the average to set
	 */
	public void setAverage(Boolean average) {
		this.average = average;
	}

	/**
	 * @see stream.data.stats.StatisticsListener#dataArrived(stream.data.stats.Statistics)
	 */
	@Override
	public void dataArrived(Statistics stats) {

		log.debug( "Current stats: {}", current );
		current.add( stats );
		log.debug( "  after add: {}", current );
		Statistics windowSum = new Statistics();
		
		window.add( stats );
		if( window.size() > windowSize ){
			Statistics first = window.remove( 0 );
			log.debug( "  substracting: {}", first );
			current.substract( first );
		} 

		for( Statistics st : window ){
			windowSum.add( st );
		}
		
		windowSum.put( "Events", stats.get( "Events" ) );
		
		Statistics windowAvg = current; //.divideBy( windowSize.doubleValue() );
		windowAvg = windowAvg.divideBy( windowSize.doubleValue() );
		windowAvg.put( "Events", stats.get( "Events" ) );
		//log.info( "  now (sum, last window): {}", windowSum );
		windowSum.setKey( "window_" + windowSize + "_" + stats.getKey() );
		if( average )
			inner.dataArrived( windowAvg );
		else
			inner.dataArrived( windowSum );
	}
}