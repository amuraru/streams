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
public class MultiRandomClassifierExperiment {
	
	/* The logger for this class */
	static Logger log = LoggerFactory.getLogger( MultiRandomClassifierExperiment.class );
	
	public void runExperiment(){
		
	}
	
	
	public static void main( String[] args ) throws Exception {
		URL url = MultiRandomClassifierExperiment.class.getResource( "/MultiRandomClassifier-experiment.xml" );
		ExperimentRunner.main( new String[]{ url.toString() } );
	}
}
