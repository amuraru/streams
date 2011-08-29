/**
 * 
 */
package stream.counter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import stream.counter.hashing.HashFunctionFactory;
import stream.counter.hashing.SimpleHashFactory;
import stream.model.Distribution;

/**
 * @author chris
 *
 */
public class CountSketchDistribution<T extends Serializable> 
	extends CountSketch<T> 
	implements Distribution<T> 
{
	/** The unique class ID */
	private static final long serialVersionUID = 6642423530289424213L;

	Integer domain = 100;
	Integer hashFunctions = 10;
	Integer buckets = 1000;
	Integer k = 100;
	HashFunctionFactory<T> hashFactory = new SimpleHashFactory<T>();
	
	public CountSketchDistribution(){
		this.model = null;
	}
	
	
	public CountModel<T> getModel(){
		
		if( this.model == null )
			this.model = new CountSketchModel<T>( getDomain(), getHashFunctions(), getBuckets(), getK(), hashFactory );
		
		return super.getModel();
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
		if( this.model == null )
			getModel();
		super.learn(item);
	}

	
	
	//
	//
	// getters and setters
	//
	//
	

	/**
	 * @return the domain
	 */
	public Integer getDomain() {
		return domain;
	}


	/**
	 * @param domain the domain to set
	 */
	public void setDomain(Integer domain) {
		this.domain = domain;
	}


	/**
	 * @return the hashFunctions
	 */
	public Integer getHashFunctions() {
		return hashFunctions;
	}


	/**
	 * @param hashFunctions the hashFunctions to set
	 */
	public void setHashFunctions(Integer hashFunctions) {
		this.hashFunctions = hashFunctions;
	}


	/**
	 * @return the buckets
	 */
	public Integer getBuckets() {
		return buckets;
	}


	/**
	 * @param buckets the buckets to set
	 */
	public void setBuckets(Integer buckets) {
		this.buckets = buckets;
	}

	/**
	 * @return the k
	 */
	public Integer getK() {
		return k;
	}

	/**
	 * @param k the k to set
	 */
	public void setK(Integer k) {
		this.k = k;
	}


	/**
	 * @see stream.model.Distribution#getElements()
	 */
	@Override
	public Set<T> getElements() {
		return getModel().keySet();
	}
}