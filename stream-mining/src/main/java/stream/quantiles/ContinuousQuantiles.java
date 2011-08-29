/**
 * 
 */
package stream.quantiles;

import java.util.LinkedList;

import stream.model.SelectiveDescriptionModel;


/**
 * <p>
 * This is an implementation of the continuous quantile estimator proposed
 * by Lin et.al. in <i>Continuously Maintaining Quantile Summaries of the
 * Most Recent N Elements over a Data Stream</i>.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class ContinuousQuantiles implements QuantileLearner {

	/** The unique class ID */
	private static final long serialVersionUID = 7796683231072267893L;
	
	// the list of buckets
	//
	LinkedList<GKQuantiles> buckets = new LinkedList<GKQuantiles>();
	
	
	// the error factor epsilon
	Double eps = 0.05;
	
	// the maximum number of buckets
	Integer maxBuckets = 10;
	
	// the number of items processed by this learner
	Integer N = 0;
	
	
	/**
	 * This creates a new continuous learner, starting with an empty list
	 * of buckets.
	 * 
	 * @param eps
	 * @param maxBuckets
	 */
	public ContinuousQuantiles( double eps, int maxBuckets ){
		this.eps = eps;
		this.maxBuckets = maxBuckets;
		N = 0;
		buckets = new LinkedList<GKQuantiles>();
	}
	




	/**
	 * @see stream.learner.Learner#init()
	 */
	@Override
	public void init() {
		buckets = new LinkedList<GKQuantiles>();
	}


	/**
	 * @return the eps
	 */
	public Double getEpsilon() {
		return eps;
	}




	/**
	 * @param eps the eps to set
	 */
	public void setEpsilon(Double eps) {
		this.eps = eps;
	}




	/**
	 * @return the maxBuckets
	 */
	public Integer getBuckets() {
		return maxBuckets;
	}




	/**
	 * @param maxBuckets the maxBuckets to set
	 */
	public void setBuckets(Integer maxBuckets) {
		this.maxBuckets = maxBuckets;
	}




	/**
	 * @return the n
	 */
	public Integer getN() {
		return N;
	}




	/**
	 * @param n the n to set
	 */
	public void setN(Integer n) {
		N = n;
	}



	/**
	 * @see edu.udo.cs.pg542.util.learner.Learner#getModel()
	 */
	@Override
	public SelectiveDescriptionModel<Double, Double> getModel() {
		//
		// TODO: we need to implement
		//
		
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * @see edu.udo.cs.pg542.util.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn(Double item) {
		GKQuantiles currentBucket = buckets.getFirst();
		if( currentBucket.getCount() >= Math.floor( eps * 0.5 * N.doubleValue() ) ){
			buckets.removeLast();
			currentBucket = new GKQuantiles( eps * 0.5d );
		}
		currentBucket.learn( item );
		N++;
	}
}