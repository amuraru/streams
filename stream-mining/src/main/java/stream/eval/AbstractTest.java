/**
 * 
 */
package stream.eval;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import stream.data.stats.Statistics;
import stream.learner.Learner;


/**
 * <p>
 * This class implements an abstract test of a set of learners. The test
 * is carried out on some specified test datum.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 * @param <D> The generic type of data that the learners can be tested upon.
 * @param <L> The learner type. All learners need to reflect a common generic type.
 */
public abstract class AbstractTest<D, L extends Learner<D,?>> implements Evaluation<D, L>, Test<D, L> {

	L baseline;
	Map<String,L> learner;
	Map<String,String> properties = new TreeMap<String,String>();
	private Map<String,ConfusionMatrix<String>> confusions = new LinkedHashMap<String,ConfusionMatrix<String>>();
	
	public AbstractTest( L baseLine, Map<String,L> learner ){
		this.baseline = baseLine;
		this.learner = new LinkedHashMap<String,L>( learner );
	}
	
	
	/**
	 * @see stream.eval.Evaluation#getBaselineLearner()
	 */
	public L getBaselineLearner(){
		return baseline;
	}
	
	
	public void setBaselineLearner( L base ){
		this.baseline = base;
	}
	
	
	/**
	 * @see stream.eval.Evaluation#getLearnerCollection()
	 */
	public Map<String,L> getLearnerCollection(){
		return learner;
	}	
	
	
	/**
	 * @see stream.eval.Evaluation#addLearner(java.lang.String, L)
	 */
	public void addLearner( String name, L learnAlgorithm ){
		learner.put( name, learnAlgorithm );
	}
	
	
	public Map<String,String> getProperties(){
		return properties;
	}
	
	public void setProperty( String key, String val ){
		if( val == null )
			properties.remove( key );
		else
			properties.put( key, val );
	}
	
	public String getProperty( String key ){
		return properties.get( key );
	}
	
	/* (non-Javadoc)
	 * @see stream.eval.Test#test(D)
	 */
	public abstract Statistics test( D data );
	
	
	public ConfusionMatrix<String> getConfusionMatrix( String learner ){
		ConfusionMatrix<String> m = confusions.get( learner );
		if( m == null ){
			m = new ConfusionMatrix<String>();
			confusions.put( learner, m );
		}
		return m;
	}
	
	
	public abstract Statistics test( Map<String,L> learner, D data );
	
	
	public String toString(){
		StringBuffer s = new StringBuffer();
		String pre = "test.";
		
		for( String key : properties.keySet() )
			s.append( pre + "parameter." + key + "=" + getProperty( key ) + "\n" );
		
		s.append( pre + "baseline=" + baseline.toString() + "\n" );
		
		for( String key : learner.keySet() )
			s.append( pre + "learner." + key + "=" + learner.get( key ).toString() + "\n" );
		
		return s.toString();
	}
}