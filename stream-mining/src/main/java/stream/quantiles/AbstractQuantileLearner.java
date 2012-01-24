/**
 * 
 */
package stream.quantiles;

import stream.data.Data;


/**
 * @author chris
 *
 */
public abstract class AbstractQuantileLearner implements QuantileLearner {

	/** The unique class ID */
	private static final long serialVersionUID = -8371217254392571620L;
	
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


	public Double getValue( Data item ){
		
		if( feature != null ){
			try {
				Double d = (Double) item.get( feature );
				return d;
			} catch (Exception e) {
				return new Double( "" + item.get( feature ) );
			}
		}
		
		return null;
	}
	
	public Data process( Data item ){
		Double value = getValue( item );
		if( value != null && ! Double.isNaN( value ) ){
			this.process( value );
		}
		return item;
	}
	
	public abstract void process( Double value );
}
