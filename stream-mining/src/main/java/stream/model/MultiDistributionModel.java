/**
 * 
 */
package stream.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * The multi-distribution model is a collection of distribution models, 
 * each of which captures the distribution of a single attribute.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class MultiDistributionModel implements Model {

	/** The unique class ID */
	private static final long serialVersionUID = -7554164941204905755L;
	static Logger log = LoggerFactory.getLogger( MultiDistributionModel.class );
	
	Integer bins = 10;
	Map<String,Distribution<?>> models = new HashMap<String,Distribution<?>>();
	
	public MultiDistributionModel( Integer bins ){
		this.bins = bins;
	}
	
	@SuppressWarnings("unchecked")
	public void update( Map<String,?> datum ){
		for( String key : datum.keySet() ){
			
			if( datum.get( key ).getClass().equals( Double.class ) ){
				Distribution<Double> m = (Distribution<Double>) models.get( key );
				if( m == null ){
					m = new NumericalDistributionModel( bins, 1.0d );
					models.put( key, m );
				}
				//log.info( "Updating model {}", key );
				m.update( (Double) datum.get(key) );
			} else {
				Distribution<String> m = (Distribution<String>) models.get( key );
				if( m == null ){
					m = new NominalDistributionModel<String>();
					models.put( key, m );
				}
				m.update( datum.get(key) + "" );
			}
				
		}
	}
	
	public List<String> getKeys(){
		return new LinkedList<String>( models.keySet() );
	}
	
	public Distribution<?> getDistribution( String key ){
		return models.get( key );
	}
}
