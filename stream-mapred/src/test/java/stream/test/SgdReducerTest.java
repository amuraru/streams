package stream.test;

import java.net.URL;

import stream.optimization.SgdReducer;

public class SgdReducerTest {

	
	
	public void testReduceAvg() throws Exception {
		URL url = SgdReducerTest.class.getResource( "/reduce-test.data" );
		SgdReducer reducer = new SgdReducer();
		reducer.reduce( url.openStream(), System.out );
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SgdReducerTest test = new SgdReducerTest();
		test.testReduceAvg();
	}
}
