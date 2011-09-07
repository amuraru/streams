package stream.optimization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsStreamWriter;
import stream.eval.LossFunction;
import stream.eval.PredictionError;
import stream.io.DataStream;
import stream.io.SvmLightDataStream;
import stream.learner.Perceptron;

public class SGDTest {

	static Logger log = LoggerFactory.getLogger( SGDTest.class );



	public static List<Data> read( DataStream stream, int num ){
		List<Data> dataset = new ArrayList<Data>();

		int i = 0;
		try {
			Data data = stream.readNext();
			while( i < num && data != null ){
				dataset.add( data );
				data = stream.readNext();
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataset;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final Statistics stats = new Statistics();

		String url = "http://kirmes.cs.uni-dortmund.de/data/ccat.tr";
		url = "file:///Users/chris/sgd/rcv1_ccat/ccat.tr";
		//url = "file:///Users/chris/sgd/adult/adult.tr";
		SvmLightDataStream stream = new SvmLightDataStream( url );
		stream.setLimit( 10000L );
		String testUrl = "file:///Users/chris/sgd/rcv1_ccat/ccat.tt";
		//testUrl = "file:///Users/chris/sgd/adult/adult.tt";

		long start = System.currentTimeMillis();

		List<Data> train = read( stream, 10000 );
		log.info( "{} examples read in {} ms.", train.size(), System.currentTimeMillis() - start );

		Collections.shuffle( train, new Random( System.currentTimeMillis() ) );
		
		start = System.currentTimeMillis();
		List<Data> test = read( new SvmLightDataStream( testUrl ), 10000 );
		log.info( "{} test examples read in {}ms", test.size(), System.currentTimeMillis() - start );

		HingeLoss loss = new HingeLoss();
		loss.setLambda( 1.0e-6 );
		StochasticGradientDescent sgd = new StochasticGradientDescent( loss );
		sgd.init();
		sgd.setD( 1000.0d );

		start = System.currentTimeMillis();

		Perceptron perceptron = new Perceptron();
		
		StatisticsStreamWriter normPlot = new StatisticsStreamWriter( new File( "/tmp/squared-norm.stats" ) );
		int limit = Integer.MAX_VALUE;
		int i = 0;
		int passes = 1;
		
		List<Double> labels = new ArrayList<Double>();
		labels.add( 1.0 );
		labels.add( -1.0d );
		
		
		// multi-pass loop for iterating over the stream multiple times
		//
		for( int pass = 0; pass < passes; pass++ ){
			log.info( "pass {}", pass );
			
			// the training loop for the classifiers
			//
			for( Data item : train ){
				
				perceptron.learn( item );
				//sgd.learn( item );
				
				//
				// log the squared norm value every 1000th example
				//
				if( i > 0 && i % 1000 == 0 ){
					double norm = sgd.w.snorm();
					stats.clear();
					stats.add( "iteration", new Double(i) );
					stats.add( "sq-norm", norm );
					normPlot.dataArrived( stats );
				}
				i++;
				if( i > limit )
					break;
			}
		}

		log.info( "{} training passes completed.", passes );
		log.info( "Training on {} examples required {} ms", train.size(), (System.currentTimeMillis()-start) );

		
		// setup of the prediction error
		//
		PredictionError<Double> performance = new PredictionError<Double>();
		performance.setLossFunction( new LossFunction<Double>(){
			@Override
			public double loss(Double x1, Double x2) {
				if( x1 * x2 > 0 )
					return 0;
				
				return 1;
			}
		});
		
		//performance.addLearner( "SGD", sgd );
		performance.addLearner( "Perceptron", perceptron );
		

		
		// iterate over the test-set and determine the prediction error
		// using the performance data-processor
		//
		start = System.currentTimeMillis();
		int pos = 0;
		int neg = 0;
		
		for( Data item : test ){
			performance.process( item );
			
			/*
			if( sgd.predict( item ) < 0 )
				neg++;
			else
				pos++;
			 */
		}
		
		log.info( "sgd predicted {} times -1 and {} times +1", neg, pos );
		
		log.info( "Testing on {} examples required {} ms", test.size(), (System.currentTimeMillis() - start ) );
		
		
		// output of the accuracy for all learners
		//
		for( String learnerName : performance.getLearnerCollection().keySet() ){
			log.info( "accuracy( {} ) = {}", learnerName, performance.getConfusionMatrix( learnerName ).calculateAccuracy() );
		}		
		
		
		// (optional)
		// output of the confusion matrix for all learners
		//
		for( String learnerName : performance.getLearnerCollection().keySet() ){
		log.info( "-----CONFUSION_MATRIX FOR   {}-------------------------------------\n", learnerName );
			log.info( "{}", performance.getConfusionMatrix( learnerName ));
		}
		log.info( "------------------------------------------------\n" );
	}
}