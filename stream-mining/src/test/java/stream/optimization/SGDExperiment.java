package stream.optimization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.plotter.StreamPlotter;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsStreamWriter;
import stream.eval.LossFunction;
import stream.eval.PredictionError;
import stream.eval.TestAndTrain;
import stream.io.DataStream;
import stream.io.SvmLightDataStream;
import stream.learner.Classifier;
import stream.learner.Learner;
import stream.learner.Perceptron;

public class SGDExperiment {

    static Logger log = LoggerFactory.getLogger( SGDExperiment.class );

    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	TestAndTrain<Data,Classifier<Data,Double>> evaluation = new TestAndTrain<Data,Classifier<Data,Double>>( new PredictionError() );
	List<Classifier<Data,Double>> classifiers = new ArrayList<Classifier<Data,Double>>();
	
	File output;
	
	
	
	public SGDExperiment( File outputDirectory ) throws Exception {
		if( !outputDirectory.isDirectory() )
			outputDirectory.mkdirs();
		
		output = outputDirectory;
		

    	StreamPlotter errorPlot = new StreamPlotter( "Events", new File( output.getAbsolutePath() + File.separator + "model-error.png" ) );
		errorPlot.setTitle( "Model Error" );
		evaluation.addPerformanceListener( errorPlot );
	}
	
    
    @Test
    public void testDummy(){
    }
    

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

    
    public void train( Classifier<Data,Double> learner, Collection<Data> trainingData ){
    	List<Classifier<Data,Double>> learners = new ArrayList<Classifier<Data,Double>>();
    	learners.add( learner );
    	train( learners, trainingData );
    }
    
    
    public void train( Collection<Data> trainingData ){
    	long start = System.currentTimeMillis();
    	
    	for( Data item : trainingData ){
    		evaluation.dataArrived( item );
    	}
    	
    	log.info( "Training {} classifiers required {} ms", evaluation.getLearner().size(), System.currentTimeMillis() - start );
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void train( List<Classifier<Data,Double>> learner, Collection<Data> trainingData ){
    	
		PredictionError performance = new PredictionError();
    	for( Learner l : learner ){
    		performance.addLearner( l.toString(), l );
    	}
    			
    	long start = System.currentTimeMillis();
    	//
    	//
    	for( Data item : trainingData ){
    		for( Learner<Data,?> learningAlgo : learner ){
    			learningAlgo.learn( item );
    		}
    	}
    	
    	log.info( "Training of {} algorithms required {} ms", learner.size(), System.currentTimeMillis() - start );
    }
    
    
    public void test( Collection<Data> testData ){
    	
    }
    
    
    public void init(){
    	for( Learner<Data,?> l : evaluation.getLearner().values() ){
    		l.init();
    	}
    	
    	
    }
    
    public void add( Classifier<Data,Double> classifier ){
    	evaluation.addLearner( classifier.toString(), classifier );
    	classifiers.add( classifier );
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        try {
        	
        	File output = new File( "/tmp/output" );
        	if( ! output.isDirectory() )
        		output.mkdirs();
        	final Statistics stats = new Statistics();
        	
        	String url = "http://kirmes.cs.uni-dortmund.de/data/ccat.tr";
        	url = "file:///Users/chris/sgd/rcv1_ccat/ccat.tr";
        	url = "file:///Users/chris/sgd/adult/adult.tr";
        	SvmLightDataStream stream = new SvmLightDataStream( url );
        	stream.setLimit( 10000L );
        	String testUrl = "file:///Users/chris/sgd/rcv1_ccat/ccat.tt";
        	testUrl = "file:///Users/chris/sgd/adult/adult.tt";

        	
        	List<Data> trainingData = read( stream, 10000 );
        	List<Data> testData = null;
        	
        	
        	SGDExperiment experiment = new SGDExperiment( output );
        	
        	
        	
        	experiment.init();

        	
        	for( int i = 0; i < 10; i++ ){
        		Collections.shuffle( trainingData );
        		experiment.train(trainingData);
        	}
        	
        	experiment.testDummy();
        	
        	

            long start = System.currentTimeMillis();

            List<Data> train = read( stream, 10000 );
            log.info( "{} examples read in {} ms.", train.size(), System.currentTimeMillis() - start );

            //Collections.shuffle( train, new Random( System.currentTimeMillis() ) );

            start = System.currentTimeMillis();
            List<Data> test = read( new SvmLightDataStream( testUrl ), 10000 );
            log.info( "{} test examples read in {}ms", test.size(), System.currentTimeMillis() - start );

            HingeLoss loss = new HingeLoss();
            loss.setLambda( 1.0e-6 );
            StochasticGradientDescent sgd = new StochasticGradientDescent( loss );
            sgd.init();
            sgd.setD( 1000.0d );

            WindowedSGD winSgd = new WindowedSGD( loss );
            winSgd.init();
            winSgd.setD( 1000.0d );
            winSgd.setWindowSize( 5000 );
            winSgd.setPasses( 10 );

            start = System.currentTimeMillis();

            Perceptron perceptron = new Perceptron();

            StatisticsStreamWriter normPlot = new StatisticsStreamWriter( new File( "/tmp/squared-norm.stats" ) );
            int limit = Integer.MAX_VALUE;
            int i = 0;
            int passes = 1;


            // multi-pass loop for iterating over the stream multiple times
            //
            for( int pass = 0; pass < passes; pass++ ){
                log.info( "pass {}", pass );

                // the training loop for the classifiers
                //
                for( Data item : train ){

                    perceptron.learn( item );
                    sgd.learn( item );
                    winSgd.learn( item );

                    //
                    // log the squared norm value every 1000th example
                    //
                    if( i > 0 && i % 1000 == 0 ){
                        double norm = sgd.w.norm();
                        double wnorm = winSgd.w.snorm();
                        stats.clear();
                        stats.add( "iteration", new Double(i) );
                        stats.add( "sq-norm", norm );
                        stats.add( "windowed.sq-norm", wnorm );
                        normPlot.dataArrived( stats );
                        log.info( "Iteration {}", i );
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

            performance.addLearner( "SGD", sgd );
            performance.addLearner( "Perceptron", perceptron );
            performance.addLearner( "WindowedSGD", winSgd );

            // iterate over the test-set and determine the prediction error
            // using the performance data-processor
            //
            start = System.currentTimeMillis();
            double pos = 0;
            double neg = 0;

            for( Data item : test ){
                performance.process( item );

                Double label = (Double) item.get( "@label" );
                if( label < 0.0d )
                    neg += 1.0d;
                else
                    pos += 1.0d;
            }

            log.info( "sgd predicted {} times -1 and {} times +1", neg, pos );

            log.info( "Testing on {} examples required {} ms", test.size(), (System.currentTimeMillis() - start ) );
            // output of the accuracy for all learners
            //
            for( String learnerName : performance.getLearnerCollection().keySet() ){
                log.info( "accuracy( {} ) = {}", learnerName, performance.getConfusionMatrix( learnerName ).calculateAccuracy() );
            }		

            double major = pos / (pos+neg);
            if( neg > pos )
                major = neg / (pos+neg);
            log.info( "Error of majority vote is: {}", major );

        } catch (Exception e) {
            log.error( "Failed to run test: {}", e.getMessage() );
            e.printStackTrace();
        }
    }
}