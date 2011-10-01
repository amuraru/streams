/**
 * 
 */
package stream.reducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.stats.Statistics;
import stream.mapred.StreamReducer;

/**
 * <p>
 * This reducer simple sums up all data items from its input. Any nominal attributes
 * will simply be ignored and are thus not reflected in the final output.
 * </p> 
 * 
 * <p>
 * If the <code>resultUrl</code> attribute is set, then the sum will be posted to that 
 * URL, which may record the data. In addition to that, a special attribute <code>@timestamp</code>
 * will be added with the current time.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class Sum extends StreamReducer {

	static Logger log = LoggerFactory.getLogger( Sum.class );
	protected String resultUrl;
	protected Statistics stats;

	
	/**
	 * @return the resultUrl
	 */
	public String getResultUrl() {
		return resultUrl;
	}

	
	/**
	 * @param resultUrl the resultUrl to set
	 */
	public void setResultUrl(String resultUrl) {
		this.resultUrl = resultUrl;
	}

	
	public void init() throws Exception {
		stats = new Statistics();
	}
	
	
	public Data process( Data item ){
		if( item != null ){
			log.debug( "reducing test result: {}", item );
			for( String key : item.keySet() ){
				if( item.get( key ) instanceof Double ){
					stats.add( key, (Double) item.get(key) );
				}
			}
		}
		return item;
	}
	

	public void finish() throws Exception {
		
		try {
			log.debug( "Writing reduced results to output stream..." );
			Data results = new DataImpl();
			results.putAll( stats );
			write( results );
		} catch (Exception e){
			e.printStackTrace();
		}

	}
}