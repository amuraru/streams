package stream.counter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import stream.model.Model;

/**
 * <p>
 * {@link Model}-part of the implementation of the Space-Saving algorithm described in the paper
 * "Efficient Computation of Frequent and Top-k Elements in Data Streams" written by 'Ahmed Metwally', 'Divyakant Agrawal'
 * and 'Amr El Abbadi'
 * </p>
 * 
 * @author Lukas Kalabis
 * @see SpaceSaving
 *
 * @param <T>
 */

public class SpaceSavingModel<T extends Serializable> implements DynamicFrequentItemModel<T> {

	private static final long serialVersionUID = 1L;

	/**
     * <p>The data structure which holds all counting information.</p>
     */
    private final List<Bucket<List<T>>> dataStructure;
        
    /**
     * The total count of all counted elements
     * in the stream so far.
     */
    private long elementsCounted;

    private double maxError;
    
    private boolean guaranteed = true;
    
    public SpaceSavingModel(int counters, double support, double maxError){
    	if(support <= 0 || support >= 1) {
    	    throw new IllegalArgumentException("Support has to be > 0 and < 1.");
    	}
    	if(counters <= 0 ){
    		throw new IllegalArgumentException("Counters has to be > 0");
    	}
    	
    	elementsCounted = 0L;
    	this.maxError = maxError;
    	dataStructure = new LinkedList<Bucket<List<T>>>();
    	for(int i = 0 ; i < counters; i++) {
    		dataStructure.add(new Bucket<List<T>>(new LinkedList<T>(), 0, 0));
    	}
    }
    
    public boolean containsItem(T item){
    	Bucket<List<T>> bucket = getBucketForItem(item);
    	if (bucket != null){
    		return true;
    	}else{
    		return false;
    	}
    }
    /**
     * <p>Increment the count frequency of the provided item by 1.</p>
     * <p>
     * First it checks two thinks.
     * <li>Is the Bucket which count has to be updated the last bucket?</li>
     * <li>Is the new frequency of the Bucket the same as the neighbor bucket frequency?</li>
     * If one of this is true the bucket with the least hits will be deleted and the new item will
     * get this bucket.<br/>
     * Otherwise: The item will be added into the neighbor bucket and removed from the original bucket.
     * </p>
     * <p>
     * In the end the whole data structure is sorted
     * </p>
     *
     * @param item The item whose frequency shall be incremented by 1.
     */
    void incrementCount(T item) {
    	Bucket<List<T>> firstBucket = getBucketForItem(item);
    	long bucketCount = firstBucket.frequency+1;
    	boolean replaceOldBucket = false;
    	if(dataStructure.indexOf(firstBucket) == dataStructure.size()-1){
    		replaceOldBucket = true;
    	}else if(dataStructure.get(dataStructure.indexOf(firstBucket)+1).frequency != bucketCount){
    		replaceOldBucket = true;
    	}else{
    		Bucket<List<T>> neighborBucket = dataStructure.get(dataStructure.indexOf(firstBucket)+1);
    		neighborBucket.item.add(item);
    		firstBucket.item.remove(item);
    	}
    	if(replaceOldBucket){
    		Bucket<List<T>> bucket = dataStructure.get(0);
    		long oldMaxError = bucket.getMaxError();
    		bucket.item.clear();
    		bucket.item.add(item);
    		bucket.frequency++;
    		bucket.setMaxError(oldMaxError);
    	}
    	elementsCounted++;
    	sortDataStructure();
    }
       
   /**
    * <p>
    * This method insert a new, not yet seen item, into the data structure.
    * <br />
    * The bucket with the least hits will be cleared and the new item will get this
    * bucket. Also the frequency will be increment.
    * <br />
    * In the last step the whole data structure will be sorted
    * </p>
    * @param item The item that is inserted into the model.
    */
    public void insertElement(T item){
    	Bucket<List<T>> bucket = dataStructure.get(0);
    	bucket.item.clear();
		bucket.item.add(item);
		bucket.frequency++;
		elementsCounted++;
		sortDataStructure();    		
    }
    
    /**
     * <p>
     * This method sort the data structure by the frequency.
     * </p>
     */
    private void sortDataStructure(){
    	Collections.sort(dataStructure, new Comparator<Bucket<List<T>>>() {

			@Override
			public int compare(Bucket<List<T>> o1, Bucket<List<T>> o2) {
				return Long.valueOf(o1.frequency).compareTo(o2.frequency);
			}
		});
    }
    
    /**
     * <p>
     * This method returns the bucket which contains the item or null
     * if the items is in no bucket.
     * </p>
     * @param item The item which you search for in all buckets 
     * @return Bucket with the item or null if Element is not enclosed.
     */
    private Bucket<List<T>> getBucketForItem(T item){
    	for(Bucket<List<T>> b : dataStructure){
    		if(b.item.contains(item)){
    			return b;
    		}
    	}
    	return null;
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	public Long getTotalCount() {
		return elementsCounted;
	}   

	@Override
	public Collection<T> getFrequentItems(double minSupport) {
		
		Collection<T> result = new ArrayList<T>();
		int j = 1;
		for(Bucket<List<T>> b : dataStructure){
			if(((b.frequency - maxError ) > (minSupport * elementsCounted)) && (j <= dataStructure.size())){
				for(int i = 0; i < b.item.size(); i++){
					result.add(b.item.get(i));
				}
				if((b.frequency - maxError) < (minSupport * elementsCounted)){
					guaranteed = false;
				}
			}
			j++;
		}
        return result;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public Set<T> keySet() {
		return null;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public Long predict(T item) {
		if (dataStructure.contains(item)) {
            return getBucketForItem(item).frequency;
        }
        return 0L;
	}
	
	/**
	 * Shows if the frequent Items are still in the guaranteed
	 * bounds of the algorithm.
	 * @return
	 */
	public boolean getGuaranteed(){
		return guaranteed;
	}
	
	@SuppressWarnings("hiding")
	private class Bucket<T> extends CountEntryWithMaxError<T>{
		
		private static final long serialVersionUID = 1L;

		public Bucket(T item, long frequency, long maxError){
			super(item, frequency, maxError);
		}
		
		public void setMaxError(long maxError){
			this.maxError = maxError;
		}
		public long getMaxError(){
			return this.maxError;
		}
	}

	/* (non-Javadoc)
	 * @see stream.counter.CountModel#getCount(java.io.Serializable)
	 */
	@Override
	public Long getCount(T value) {
		return predict( value );
	}
}