/**
 * 
 */
package stream.reducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
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
public class Identity extends StreamReducer {

	static Logger log = LoggerFactory.getLogger( Identity.class );


	/**
	 * @see stream.mapred.Reducer#init()
	 */
	@Override
	public void init() throws Exception {
	}
	

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	public Data process( Data item ){
		try {
			this.write( item );
		} catch (Exception e) {
			log.error( "Failed to write item: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}
		return item;
	}


	/**
	 * @see stream.mapred.Reducer#finish()
	 */
	@Override
	public void finish() throws Exception {
	}
}