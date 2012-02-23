/**
 * 
 */
package stream.counter;

import java.io.Serializable;
import java.util.Set;

import stream.data.Data;

/**
 * @author chris
 *
 */
public abstract class AbstractCounter<T extends Serializable> 
	implements Counter<T>, Serializable
{
	/** The unique class ID */
	private static final long serialVersionUID = -2382989742231004064L;
	String feature;
	
	
	/**
	 * @return the feature
	 */
	public String getFeature() {
		return feature;
	}


	/**
	 * @param feature the feature to set
	 */
	public void setFeature(String feature) {
		this.feature = feature;
	}


	@SuppressWarnings("unchecked")
	public T getValue( Data data ){
		
		if( feature != null )
			return (T) data.get( feature );
		
		return null;
	}
	
	public void init(){
	}
	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		T value = getValue( data );
		if( value != null ){
			this.count( value );
		}
		
		return data;
	}


	/* (non-Javadoc)
	 * @see stream.mining.Counter#keySet()
	 */
	@Override
	public Set<T> keySet() {
		return getModel().keySet();
	}


	/* (non-Javadoc)
	 * @see stream.mining.Counter#getCount(java.lang.Object)
	 */
	@Override
	public Long getCount(T val) {
		return getModel().getCount( val );
	}


	/* (non-Javadoc)
	 * @see stream.mining.Counter#getTotalCount()
	 */
	@Override
	public Long getTotalCount() {
		return getModel().getTotalCount();
	}


	/* (non-Javadoc)
	 * @see stream.mining.Counter#count(java.lang.Object)
	@Override
	public void count(T value) {
		getModel().count( value );
	}
	 */
}