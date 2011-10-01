/**
 * 
 */
package stream.mapred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is here for backward compatibility. The new way to start the Map-and-Reduce
 * process is to call the MapReduce class as main starting point. 
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 */
public class MapReduceRunner {

	static Logger log = LoggerFactory.getLogger( MapReduceRunner.class );
	
	/**
	 * This method simply calls the main() method of the MapReduce class
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		log.warn( "Use of the 'stream.mapred.MapReduceRunner' is deprecated, please use 'stream.mapred.MapReduce'!" );
		MapReduce.main( args );
	}
}