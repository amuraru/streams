/**
 * 
 */
package test;

import java.util.Random;

import stream.data.stats.Statistics;

/**
 * @author chris
 *
 */
public class ChartTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Random rnd = new Random();
		
		Statistics st = new Statistics();
		st.put( "y", rnd.nextDouble() );
		st.put( "x", 1.0d );
	}
}
