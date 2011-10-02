/**
 * 
 */
package stream.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.remote.RemoteLogger;

/**
 * @author chris
 *
 */
public class ExperimentLog {
	
	static Logger log = LoggerFactory.getLogger( ExperimentLog.class );
	public final static Integer INFO = 0;
	public final static Integer ERROR = 1;
	public final static Integer STATUS = 2;
	public final static Integer DEBUG = 3;

	private static ExperimentLog logger = new ExperimentLog();
	RemoteLogger rlog;
	
	private ExperimentLog(){
		try {
			log.info( "Creating new ExperimentLog instance with url {}", System.getProperty( "experiment.log.url" ) );
			URL url = new URL( System.getProperty( "experiment.log.url" ) );
			rlog = new RemoteLogger( url );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ExperimentLog( URL url ){
		rlog = new RemoteLogger( url );
	}
	
	public static String getExperimentName(){
		if( System.getProperty( "experiment.name" ) != null )
			return System.getProperty( "experiment.name" );
		
		String id = MD5.md5( System.currentTimeMillis() );
		System.setProperty( "experiment.name", id );
		return id;
	}
	
	public static void log( String msg ){
		log( getExperimentName(), msg );
	}

	public static void log( String msg, Object ... o ){
		log( getExperimentName(), msg, o );
	}
	
	public static void log( Integer level, String msg ){
		log( level, getExperimentName(), msg );
	}
	
	public static void log( Integer level, String msg, Object ... o ){
		log( level, getExperimentName(), o );
	}

	public static void log( String key, String msg ){
		log( INFO, key, msg );
	}
	
	public static void log( String key, String msg, Object ... o ){
		log( INFO, key, expand( msg, o ) );
	}
	
	public static void log( Integer level, String key, String msg, Object ... o ){
		log( level, key, expand( msg, o ) );
	}
	
	public static void log( Integer level, String key, String msg ){
		if( logger.rlog == null ){
			log.warn( "No log-url defined in property 'experiment.log.url', not logging this message!" );
			return;
		}
		logger.rlog.log(level, key, msg );
	}

	
	public static String expand( String msg, Object ... o ){
		
		int i = 0;
		int idx = msg.indexOf( "{}" );
		while( idx >= 0 && i < o.length ){
			String repl = "null";
			if( o[i] != null )
				repl = o[i].toString();
			msg = msg.substring( 0, idx ) + repl + msg.substring( idx + 2 );
			idx = msg.indexOf( "{}" );
			i++;
		}
		
		return msg;
	}
}