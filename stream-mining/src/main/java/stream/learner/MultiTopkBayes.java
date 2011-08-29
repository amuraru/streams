/**
 * 
 */
package stream.learner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.eval.size.SizeMeasurement;

/**
 * <p>
 * This implementation of the MultiBayes learner uses the Lossy-Counting Bayes
 * implementation, which counts at fixed memory space.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class MultiTopkBayes extends MultiBayes {

	/** The unique class ID */
	private static final long serialVersionUID = 1354945765610306076L;

	/* A global logger for this class */
	static Logger log = LoggerFactory.getLogger( MultiTopkBayes.class );
	
	Integer k = 100;
	private int i = 0;
 	private Integer nestedNBs = 0;

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
	 * @see stream.learner.MultiBayes#createBayesLearner(java.lang.String)
	 */
	@Override
	protected NaiveBayes createBayesLearner(String attribute) {
		log.debug( "Creating new TopK-bayes for attribute {}", attribute );
		TopKBayes lb = new TopKBayes();
		
		if( getK() == null ){
			setK( 100 );
			log.warn( "No value set for parameter 'k', using default: {}", getK() );
		}
		lb.setK( getK() );
		lb.setLabelAttribute( attribute );
		this.nestedNBs++;
		return lb;
	}


	/**
	 * @see stream.learner.MultiBayes#learn(stream.data.Data)
	 */
	@Override
	public void learn(Data item) {
		super.learn(item);
		i++;
		if( i % 100 == 0 ){
			log.debug( "After {} envts:", i );
			log.debug( "   using {} bytes", SizeMeasurement.sizeOf( this ) );
			log.debug( "   using {} naive bayes learner", nestedNBs );
		}
	}
}