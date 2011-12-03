/**
 * 
 */
package stream.tools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.io.DataStream;

/**
 * @author chris
 *
 */
public class StreamProcess extends Thread {

	static Logger log = LoggerFactory.getLogger( StreamProcess.class );
	static Integer LAST_ID = 0;
	DataStream input;
	DataProcessor output;
	List<DataProcessor> processors = new ArrayList<DataProcessor>();
	boolean running = false;
	String processId;
	Long limit = -1L;
	
	
	public StreamProcess( String processId, DataStream input ){
		this.processId = processId;
		if( this.processId == null || "".equals( processId.trim() ) ){
			synchronized( LAST_ID ){
				this.processId = "spu:" + LAST_ID++;
			}
		}
		this.input = input;
	}
	
	public StreamProcess( String processId, DataStream input, DataProcessor output ){
		this(processId, input);
		addDataProcessor( output );
	}

	public void addDataProcessor( DataProcessor proc ){
		if( ! processors.contains( proc ) )
			processors.add( proc );
	}
	
	public void removeDataProcessor( DataProcessor proc ){
		processors.remove( proc );
	}
	
	
	/**
	 * @return the limit
	 */
	public Long getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return the processId
	 */
	public String getProcessId() {
		if( processId == null )
			processId = "spu:" + getId();
		return processId;
	}

	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		running = true;
		long cnt = 0;
		try {
			Data item = input.readNext();
			while( item != null && (limit < 0 || cnt < limit ) ){
				cnt++;
				log.info( "Processing {}", item );
				
				for( DataProcessor proc : processors ){
					log.trace( "pushing copy of item to processor {}", proc );
					proc.process( item );
				}
				item = input.readNext();
			}
		} catch (Exception e) {
			log.error( "Failed to process item: {}", e.getMessage() );
			e.printStackTrace();
		}
		log.debug( "{} items processed.", cnt );
		running = false;
	}

	public boolean isRunning(){
		return running;
	}
}