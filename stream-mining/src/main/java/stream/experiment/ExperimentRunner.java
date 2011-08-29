/**
 * 
 */
package stream.experiment;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class ExperimentRunner {

	static Logger log = LoggerFactory.getLogger( ExperimentRunner.class );
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if( args.length < 1 ){
			log.error("");
			log.error( "No experiment file given!" );
			log.error("");
			System.exit( -1 );
		}
		
		
		if( args[0].equals( "init" ) ){
			File out = new File( "empty-experiment.xml" );
			if( args.length > 1 )
				out = new File( args[1] );
			
			ReportProcessor.copyResource( "/empty-experiment.xml", new FileOutputStream( out ) );
			log.info("");
			log.info( "Initialized empty experiment file '{}'", out.getAbsolutePath() );
			log.info("");
			return;
		}
		
		Experiment exp = null;
		
		File file = new File( args[0] );
		if( !file.isFile() ){
			log.info("");
			log.error( "Experiment file '" + file + "' does not exist!" );
			
			try {
				URL url = new URL( args[0] );
				log.info( "Using experiment at URL " + url );
				exp = new Experiment( url );
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if( exp == null )
				System.exit( -1 );
		} else
			exp = new Experiment( file );
		
		log.info( "Starting experiment {}", exp );
		exp.run();
	}
}
