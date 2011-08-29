/**
 * 
 */
package stream.data.stats;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class MultiStatisticsWriter implements StatisticsListener {

	static Logger log = LoggerFactory.getLogger( MultiStatisticsWriter.class );
	File dir;
	Map<String,StatisticsStreamWriter> writer = new HashMap<String,StatisticsStreamWriter>();
	
	public MultiStatisticsWriter( File dir ){
		this.dir = dir;
	}
	
	/**
	 * @see stream.data.stats.StatisticsListener#dataArrived(stream.data.stats.Statistics)
	 */
	@Override
	public void dataArrived(Statistics stats) {
		StatisticsStreamWriter w = getWriter( stats.getKey() );
		if( w != null )
			w.dataArrived( stats );
	}
	
	
	public StatisticsStreamWriter getWriter( String key ){
		StatisticsStreamWriter w = writer.get( key );
		if( w == null ){
			File out = new File( dir.getAbsolutePath() + File.separator + key + ".dat" );
			out.getParentFile().mkdirs();
			try {
				w = new StatisticsStreamWriter( new FileOutputStream( out ) );
				log.info( "Creating new statistics writer to file {}", out );
				writer.put( key, w );
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return w;
	}
}