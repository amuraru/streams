/**
 * 
 */
package stream.eval;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.stats.Statistics;
import stream.learner.Learner;
import stream.learner.MultiLabelPredictor;
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
public class MultiPredictionError<T> extends AbstractTest<Data, Learner<Data,PredictionModel<Data,Map<String,T>>>> {

	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( MultiPredictionError.class );
	
	/* The loss function used to assess the prediction error */
	LossFunction<T> lossFunction = new LossFunction<T>(){
		@Override
		public double loss(T x1, T x2) {
			
			if( x1 == x2 )
				return 0.0d;
			
			if( x1 == null || x2 == null )
				return 1.0d;
			
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
		}
	};
	
	
	public MultiPredictionError(){
		super( new MultiLabelPredictor<T>(), new HashMap<String,Learner<Data,PredictionModel<Data,Map<String,T>>>>() );
	}
	
	
	/**
	 * @param baseLine
	 * @param learner
	 */
	public MultiPredictionError(Learner<Data, PredictionModel<Data, Map<String,T>>> baseLine,
			Map<String, Learner<Data, PredictionModel<Data, Map<String,T>>>> learner) {
		super(baseLine, learner);
	}

	
	public MultiPredictionError( Map<String,Learner<Data,PredictionModel<Data,Map<String,T>>>> learner ){
		super( null, learner );
		this.setBaselineLearner( new MultiLabelPredictor<T>() );
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
	
	
	protected String createErrorAttribute( String key ){
		return "Error(" + key + ")";
	}


	/**
	 * @see stream.eval.AbstractTest#test(java.util.Map, java.lang.Object)
	 */
	@Override
	public Statistics test( Map<String, Learner<Data, PredictionModel<Data, Map<String, T>>>> learners, Data data) {
		log.debug( "Determining prediction error for data: {}", data );
		
		Statistics error = new Statistics();
		Map<String,T> truth = this.baseline.getModel().predict( data );
		log.debug( "  true label is: {}", truth );
		Integer tests = truth.keySet().size(); 
		
		for( String key : learners.keySet() ){
			Learner<Data,PredictionModel<Data,Map<String,T>>> learner = learners.get(key);
			Map<String,T> pred = learner.getModel().predict( data );
			log.debug( "  predicted label is: {}", pred );
			
			ConfusionMatrix<String> m = getConfusionMatrix(key);
			m.add( truth.toString(), pred.toString() );
			
			String errorName = createErrorAttribute( key );
			Double errorLoss = 0.0d;
			for( String label : truth.keySet() ){
				Double err = lossFunction.loss( truth.get( label ), pred.get( label ));
				errorLoss += err;
			}
			log.debug( "  error is: {}", errorLoss );
			error.put( errorName, errorLoss / tests.doubleValue() );
		}
		
		return error;
	}
}