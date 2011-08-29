package stream.counter;

import stream.learner.Learner;

/**
 * <p>
 * {@link Learner}-part of the implementation of the Space-Saving algorithm described in the paper
 * "Efficient Computation of Frequent and Top-k Elements in Data Streams" written by 'Ahmed Metwally', 'Divyakant Agrawal'
 * and 'Amr El Abbadi'
 * </p>
 * 
 * @author Lukas Kalabis
 * @see SpaceSavingModel
 *
 * @param <T>
 */
public class SpaceSaving<T> implements Learner<T, CountModel<T>> {

	private static final long serialVersionUID = 1L;

	/**
     * Model which holds a data structure for item counting and provides methods for result extraction.
     */
	private SpaceSavingModel<T> model;

	Double support = 0.1d;
	
	Double error = 0.1d;
	
	Integer counter = 1000;
	
    /**
     * <p>Creates a new instance of SpaceSaving.</p>
     * @param counters number of available counters
     * @param support min-support for the frequency
     */
    public SpaceSaving(int counters, double support, double maxError){
    	this.counter = counters;
    	this.support = support;
    	this.error = maxError;
    	init();
    }
     
    public void init(){
    	model = new SpaceSavingModel<T>(counter, support, error);
    }
    
    /**
     * <p>
     * Increments the item count of the provided item if item is already monitored.
     * if not replace item with lowest hits count, increment the count and assign
     * the error rate
     * </p>
     * 
     * @param item The item to count
     */
	@Override
	public void learn(T item) {
		if(model.containsItem(item)){
			model.incrementCount(item);
		}else{
			/**
             * Replace the item with the lowest count with the new one.  
             */
			model.insertElement(item);
        }
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public CountModel<T> getModel() {
		return model;
	}

	/**
	 * @return the support
	 */
	public Double getSupport() {
		return support;
	}

	/**
	 * @param support the support to set
	 */
	public void setSupport(Double support) {
		this.support = support;
	}

	/**
	 * @return the error
	 */
	public Double getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(Double error) {
		this.error = error;
	}

	/**
	 * @return the counter
	 */
	public Integer getCounter() {
		return counter;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(Integer counter) {
		this.counter = counter;
	}
}