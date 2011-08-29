package stream.eval;
/**
 * 
 */


import java.util.Map;

import stream.data.stats.Statistics;
import stream.eval.size.SizeEvaluationResult;
import stream.learner.Learner;

/**
 * @author chris
 *
 */
public class MemoryUsage<D>
	implements Test<D,Learner<D,?>>
{
	public Statistics getUsage( Map<String,Learner<?,?>> learner ){
		Statistics st = new Statistics();
		
		for( String key : learner.keySet() ){
			SizeEvaluationResult size = new SizeEvaluationResult( key, learner.get( key ) );
			st.put( key, size.getModelSize() );
		}
		
		return st;
	}
	
	public Statistics getGenericUsage( Map<String,?> learner ){
		Statistics st = new Statistics();
		
		for( String key : learner.keySet() ){
			Learner<?,?> learn = (Learner<?,?>) learner.get(key);
			SizeEvaluationResult size = new SizeEvaluationResult( key, learn );
			st.put( key, size.getModelSize() );
		}
		
		return st;
	}

	/**
	 * @see stream.eval.Test#test(java.util.Map, java.lang.Object)
	 */
	@Override
	public Statistics test(Map<String, Learner<D, ?>> learner, D data) {
		return getGenericUsage( learner );
	}
}