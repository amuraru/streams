package stream.optimization;

import java.net.URL;

import stream.experiment.ExperimentRunner;

public class SGDExperimentTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			URL url = SGDExperimentTest.class.getResource( "/sgd.xml" );
			ExperimentRunner.main( new String[]{ url.toString() } );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
