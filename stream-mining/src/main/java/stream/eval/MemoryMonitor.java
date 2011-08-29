package stream.eval;
/**
 * 
 */


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;
import stream.data.stats.StatisticsListener;

/**
 * @author chris
 *
 */
public class MemoryMonitor extends Thread {
	static Logger log = LoggerFactory.getLogger( MemoryMonitor.class );
	List<StatisticsListener> listener = new ArrayList<StatisticsListener>();
	Integer testInterval = 1000;

	
	public MemoryMonitor(){
		this.setDaemon( true );
	}
	
	
	public void setTestInterval( Integer milliSeconds ){
		this.testInterval = milliSeconds;
	}
	
	public Integer getTestInterval(){
		return testInterval;
	}
	
	
	public void run(){
		
		while( true ){
			Statistics mem = getVMMemoryUsage();
			for( StatisticsListener l : listener )
				l.dataArrived( mem );
			
			try {
				Thread.sleep( testInterval );
			} catch (Exception e){
				log.error( "Failed to take my nap: {}\n{}", e.getMessage(), e );
			}
		}
	}
	
	public Statistics getVMMemoryUsage(){
		Statistics st = new Statistics();
		st.put( "TIME", new Double( System.currentTimeMillis() ) );
		Long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		st.put( "JVM-Memory", mem.doubleValue() );
		return st;
	}
	
	public void addMemoryListener( StatisticsListener l ){
		if( ! listener.contains( l ) )
			listener.add( l );
	}
	
	public void removeMemoryListener( StatisticsListener l ){
		listener.remove( l );
	}
}