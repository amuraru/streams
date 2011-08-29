/**
 * 
 */
package stream.experiment;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author chris
 *
 */
public class NaiveBayesExperiment {
	
	/* The logger for this class */
	static Logger log = LoggerFactory.getLogger( NaiveBayesExperiment.class );
	
	public void runExperiment(){
		
	}
	
	
	public static void main( String[] args ) throws Exception {
		URL url = NaiveBayesExperiment.class.getResource( "/NaiveBayes-experiment.xml" );
		ExperimentRunner.main( new String[]{ url.toString() } );
	}
}
