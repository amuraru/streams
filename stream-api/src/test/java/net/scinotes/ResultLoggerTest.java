/**
 * 
 */
package net.scinotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;

/**
 * @author chris
 *
 */
public class ResultLoggerTest {
	
	static Logger log = LoggerFactory.getLogger( ResultLoggerTest.class );

	// a test dummy
	@Test
	public void test(){
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Random rnd = new Random();
		List<Statistics> results = new ArrayList<Statistics>();
		for( int i = 0; i < 100; i++ ){
			Statistics st = new Statistics();
			st.add( "@label", rnd.nextDouble() > 0.5 ? 1.0d : -1.0d );
			
			for( int j = 0; j < 10; j++ ){
				st.add( "x" + j, rnd.nextGaussian() );
			}
			log.debug( "Adding results {}", st );
			results.add( st );
		}
		
		int sent = SciNotesAPI.send( "RND", results );
		System.out.println( sent + " elements sent" );
	}
}
