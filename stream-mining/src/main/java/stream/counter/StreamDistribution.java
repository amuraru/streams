/**
 * 
 */
package stream.counter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import stream.learner.Learner;
import stream.model.Distribution;

/**
 * <p>
 * This class is a wrapper implementation and provides the adaption of different stream counting
 * algorithms to be used as approximations to distributions of nominal attributes.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class StreamDistribution<T extends Serializable> 
	implements Serializable, Distribution<T> 
{
	/** The unique class ID */
	private static final long serialVersionUID = 2696199411688427094L;
	
	Learner<T,CountModel<T>> learner;
	
	public StreamDistribution(){
	}

	
	public StreamDistribution( Learner<T,CountModel<T>> countAlgo ){
		this.learner = countAlgo;
	}
	
	
	public void setLearner( Learner<T,CountModel<T>> learner ){
		this.learner = learner;
	}
	

	public CountModel<T> getModel(){
		return learner.getModel();
	}
	
	
	/**
	 * @see stream.model.Distribution#getCount()
	 */
	@Override
	public Integer getCount() {
		return ((Long) getModel().getTotalCount()).intValue();
	}

	/**
	 * @see stream.model.Distribution#getCount(java.io.Serializable)
	 */
	@Override
	public Integer getCount(T value) {
		return getModel().predict( value ).intValue();
	}

	/**
	 * @see stream.model.Distribution#getHistogram()
	 */
	@Override
	public Map<T, Double> getHistogram() {
		Map<T,Double> hist = new LinkedHashMap<T,Double>();
		for( T key : getModel().keySet() )
			hist.put( key, getModel().predict( key ).doubleValue() );
		return hist;
	}

	/**
	 * @see stream.model.Distribution#prob(java.io.Serializable)
	 */
	@Override
	public Double prob(T value) {
		Double val = getModel().predict( value ).doubleValue();
		Long total = (Long) getModel().getTotalCount();
		return val / total.doubleValue();
	}

	/**
	 * @see stream.model.Distribution#update(java.io.Serializable)
	 */
	@Override
	public void update(T item) {
		learner.learn( item );
	}


	/**
	 * @see stream.model.Distribution#getElements()
	 */
	@Override
	public Set<T> getElements() {
		return this.getModel().keySet();
	}
}