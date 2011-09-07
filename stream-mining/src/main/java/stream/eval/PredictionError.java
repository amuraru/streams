/**
 * 
 */
package stream.eval;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.stats.Statistics;
import stream.learner.LabelPredictor;
import stream.learner.Learner;
import stream.model.PredictionModel;


/**
 * <p>
 * This class implements a simple prediction error evaluator. Each learner of the registered
 * learner-collection is used for prediction and its result is compared to that of the baseline
 * learner model.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class PredictionError<T> 
	extends AbstractTest<Data, Learner<Data,PredictionModel<Data,T>>> 
	implements DataProcessor
{

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( PredictionError.class );
	ConfusionMatrix<String> confusionMatrix = new ConfusionMatrix<String>();
	
	/* The loss function used to assess the prediction error */
	LossFunction<T> lossFunction = new LossFunction<T>(){
		@Override
		public double loss(T x1, T x2) {
			
			
			if( x1 == x2 || x1.toString().equals( x2.toString() ) ){
				return 0.0d;
			} else
				return 1.0d;
			/*
			
			if( x1 instanceof Double && x2 instanceof Double ){
				Double d1 = (Double) x1;
				Double d2 = (Double) x2;
				return Math.abs(d1 - d2);
			} else {
				
				if( !x1.toString().equals( x2 + "" ) ){
					return 1.0d;
				}
				
			}
			
			if( x1.equals( x2 ) )
				return 0.0d;
			
			return 1.0d;
			 */
		}
	};
	
	
	public PredictionError(){
		super( null, new HashMap<String,Learner<Data,PredictionModel<Data,T>>>() );
		this.setBaselineLearner( new LabelPredictor<T>() );
	}
	
	
	/**
	 * @param baseLine
	 * @param learner
	 */
	public PredictionError(Learner<Data, PredictionModel<Data, T>> baseLine,
			Map<String, Learner<Data, PredictionModel<Data, T>>> learner) {
		super(baseLine, learner);
	}

	
	public PredictionError( Map<String,Learner<Data,PredictionModel<Data,T>>> learner ){
		super( null, learner );
		this.setBaselineLearner( new LabelPredictor<T>() );
	}
	
	
	public PredictionError( Learner<Data,PredictionModel<Data, T>> learner ){
		this();
		this.setBaselineLearner( new LabelPredictor<T>() );
		this.addLearner( learner.toString(), learner );
	}
	
	
	/**
	 * @return the lossFunction
	 */
	public LossFunction<T> getLossFunction() {
		return lossFunction;
	}


	/**
	 * @param lossFunction the lossFunction to set
	 */
	public void setLossFunction(LossFunction<T> lossFunction) {
		this.lossFunction = lossFunction;
	}


	/**
	 * @see stream.eval.AbstractTest#test(java.lang.Object)
	 */
	@Override
	public Statistics test(Data data) {
		return test( getLearnerCollection(), data );
	}
	
	
	public ConfusionMatrix<String> getConfusionMatrix(){
		return confusionMatrix;
	}
	
	
	protected String createErrorAttribute( String key ){
		return "Error(" + key + ")";
	}


	/**
	 * @see stream.eval.AbstractTest#test(java.util.Map, java.lang.Object)
	 */
	@Override
	public Statistics test( Map<String, Learner<Data, PredictionModel<Data, T>>> learners, Data data) {
		log.debug( "Determining prediction error for data: {}", data );

		Statistics error = new Statistics();
		T truth = this.baseline.getModel().predict( data );
		log.debug( "  true label is: {}", truth );
		
		for( String key : this.getLearnerCollection().keySet() ){
			log.debug( "Testing learner {}", key );
			Learner<Data,PredictionModel<Data,T>> learner = learners.get(key);
			PredictionModel<Data,T> model = learner.getModel();
			T pred = model.predict( data );
			
			ConfusionMatrix<String> m = getConfusionMatrix( key );
			m.add( truth.toString(), pred.toString() );
			log.debug( "  predicted label is: {} (true label: {})", pred, truth );
			
			String errorName = createErrorAttribute( key );
			Double errorLoss = lossFunction.loss( truth, pred );
			log.debug( "  error is: {}", errorLoss );
			error.put( errorName, errorLoss );
		}
		
		return error;
	}


	@Override
	public Data process(Data data) {
		test( data );
		return data;
	}
}