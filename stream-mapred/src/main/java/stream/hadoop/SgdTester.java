package stream.hadoop;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.vector.InputVector;
import stream.io.DataStream;
import stream.io.SparseDataStream;
import stream.io.SvmLightDataStream;
import stream.optimization.StochasticGradientDescent;
import stream.optimization.SvmHingeLoss;

public class SgdTester
implements DataProcessor
{

	static Logger log = LoggerFactory.getLogger( SgdTester.class );
	StochasticGradientDescent sgd;

	Double tested = 0.0d;
	Double errors = 0.0d;


	public SgdTester(){
		SvmHingeLoss loss = new SvmHingeLoss();   // Sang: I think it should be called as SVMPrimalHingeLoss
		double lambda = 0.0; 
		lambda = 3.08e-8; // adult
		//lambda = 1.72e-7; // mnist
		//lambda = 1.28e-6; // ccat
		//lambda = 8.82e-8; // ijcnn1
		//lambda = 7.17e-7; // covtype
		//lambda = 1.0e-8;  // mnist-1m 
		loss.setLambda( lambda );
		sgd = new StochasticGradientDescent( loss );
		//sgd.useGaussianKernel(0.001, 2048, false);
		sgd.init();

	}


	public Double getTestError(){
		if( tested == 0.0d )
			return 0.0d;

		return errors / tested;
	}


	@Override
	public Data process(Data data) {
		Double prediction = sgd.predict( sgd.getWeightVector(), data );
		Double label = new Double( data.get( "@label" ).toString() );

		if( prediction * label < 0 ){
			errors += 1.0d;
		}
		tested += 1.0d;
		return data;
	}	



	/**
	 * @param args
	 */
	public static void main(String[] params ) throws Exception {
		String[] args = params;
		args = new String[]{
				"/Users/chris/Uni/Projekte/streams/stream-mapred/OUT",
				"http://kirmes.cs.uni-dortmund.de/data/mnist-10k.tt"
		};
		
		if( args.length < 2 ){
			System.err.println( "Usage:" );
			System.err.println( "      java stream.hadoop.SgdTester WEIGHT_FILE  TEST_SET_URL" );
			System.err.println();
			System.exit(0);
		}

		File weights = new File( args[0] );
		if( ! weights.canRead() ){
			System.err.println( "Cannot open file " + weights + " for reading!" );
			System.err.println();
			return;
		}

		log.info( "Reading weight vector from {}", weights );
		List<Data> models = new ArrayList<Data>();

		
		
		SparseDataStream ms = new SparseDataStream( weights.toURI().toURL() );
		Data m = ms.readNext();
		while( m != null ){
			models.add( m );
			m = ms.readNext();
		}

		log.info( "Found {} weight vectors in file {}", models.size(), weights );
		if( models.isEmpty() ){
			System.err.println( "At least one weight vector is required for testing!" );
			System.err.println();
			return;
		}

		SgdTester test = new SgdTester();
		
		Data model = models.get( 0 );
		InputVector vec = SvmLightDataStream.createSparseVector( model );
		test.sgd.setWeightVector( vec );
		if( model.get( "b" ) != null ){
			log.info( "Using intercept {}", model.get( "b" ) );
			test.sgd.setIntercept( new Double( model.get("b").toString() ) );
		}
		
		DataStream stream = new SvmLightDataStream( new URL( args[1] ) );
		Data item = stream.readNext();
		int count = 0;
		while( item != null ){
			test.process( item );
			if( ++count % 100 == 0 ){
				log.info( "Test error after {} tests: {}", count, test.getTestError() );
				log.info( "The prediction accuracy thus is: {}", 1.0d - test.getTestError() );
			}
			item = stream.readNext();
		}
	}
}