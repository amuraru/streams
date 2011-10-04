import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class MapReduce {
	
	static Logger log = LoggerFactory.getLogger( MapReduce.class );

	/**
	 * 
	 * @param params
	 * @throws Exception
	 */
	public static void main(String[] params) throws Exception {
		log.warn( "Use of the 'MapReduce' in the default package is deprecated, please use 'stream.mapred.MapReduce'!" );
		stream.mapred.MapReduce.main( params );
	}
}