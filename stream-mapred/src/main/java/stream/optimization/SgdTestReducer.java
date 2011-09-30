/**
 * 
 */
package stream.optimization;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsPublisher;
import stream.io.DataStream;
import stream.io.DataStreamWriter;
import stream.io.SparseDataStream;
import stream.io.SparseDataStreamWriter;
import stream.mapred.StreamReducer;

/**
 * @author chris
 *
 */
public class SgdTestReducer extends StreamReducer<Data, Data> {

	static Logger log = LoggerFactory.getLogger( SgdTestReducer.class );
	DataStream inputStream;
	DataStreamWriter outputStream;
	String resultUrl;
	
	
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

	/**
	 * @see stream.mapred.StreamReducer#init(java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public void init(InputStream in, OutputStream out) {
		super.init(in, out);
		try {
			inputStream = new SparseDataStream( in );
			outputStream = new SparseDataStreamWriter( out );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.mapred.Reducer#read()
	 */
	@Override
	public Data read() {
		try {
			return inputStream.readNext();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see stream.mapred.Reducer#write(java.lang.Object)
	 */
	@Override
	public void write(Data out) {

	}

	/* (non-Javadoc)
	 * @see stream.mapred.StreamReducer#reduce()
	 */
	@Override
	public void reduce() {
		Statistics stats = new Statistics();
		
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
		

		Data item = read();
		while( item != null ){
			log.debug( "reducing test result: {}", item );
			for( String key : item.keySet() ){
				if( item.get( key ) instanceof Double ){
					stats.add( key, (Double) item.get(key) );
				}
			}

			item = read();
		}

		try {
			log.debug( "Writing reduced results to output stream..." );
			Data results = new DataImpl();
			results.putAll( stats );
			outputStream.process( results );
		} catch (Exception e){
			e.printStackTrace();
		}

		try {
			String url = resultUrl;
			if( url == null ){
				log.debug( "Checking for system property 'experiment.result.url'" );
				url = System.getProperty( "experiment.result.url" );
			}
			log.debug( "result url is: '{}'", url );
			if( url != null ){
				log.trace( "Publishing results at {}", url );
				StatisticsPublisher.publish( new URL(url), stats );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}