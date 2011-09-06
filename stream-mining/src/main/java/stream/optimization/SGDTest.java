package stream.optimization;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.DataImpl;
import stream.io.DataStream;
import stream.io.SvmLightDataStream;

public class SGDTest {

	static Logger log = LoggerFactory.getLogger( SGDTest.class );
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String url = "http://kirmes.cs.uni-dortmund.de/data/ccat.tr";
		url = "file:///Users/chris/sgd/rcv1_ccat/ccat.tr";
		DataStream train = new SvmLightDataStream( url );
		
		
		int count = 1000;
		List<DataVector> dataset = new ArrayList<DataVector>();
		for( int j = 0; j < count; j++ ){
			dataset.add( new DataVector( train.readNext( new DataImpl() ) ) );
		}
		log.info( "data read." );
		
		HingeLoss loss = new HingeLoss();
		loss.setLambda( 1.0e-6 );
		StochasticGradientDescent sgd = new StochasticGradientDescent( loss );
		sgd.init();
		sgd.setD( 1000.0d );
		long start = System.currentTimeMillis();
		
		for( int i = 0; i < dataset.size(); i++ ){
			log.info( "Training on x[{}]", i );
			sgd.learn( dataset.get( i ) );
			//sgd.printModel();
			//log.info( "Example: {}", example );
		}

		long end = System.currentTimeMillis();
		log.info( "Reading {} examples required {} ms", count, (end-start) );
	}

}
