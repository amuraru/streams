package stream.quantiles.rss;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import stream.model.SelectiveDescriptionModel;
import stream.quantiles.ProbabilisticQuantileEstimator;
import stream.quantiles.WindowSketchQuantiles;


/**
 * This class is out to realize a stream version of the rss technique presented by
 * Anna Gilbert, Yannis Kotidis, S. Muthukrishanan, and Matrin Strauss in the paper
 * "How to summarize the Universe"
 * 
 * @author Carsten Przyluczky
 *
 */
public class RandomSubsetSums  extends ProbabilisticQuantileEstimator {
	private static final long serialVersionUID = -7491178942147615981L;
	
	public final static int CANT_ESTIMATE = -1;
	public static int ELEMENTS_PER_BUCKET = 200;
	private static int MAX_BUCKET_COUNT = 5;
	private int maxValue; // represents |U| 
	
	List<Bucket> buckets;
	Bucket newestBucket = null;
	
	/**
	 * The constructor invokes all data-structure creation 
	 * 
	 * @param epsilon precision
	 * @param delta error-probability
	 * @param maxValue the maximum value the will be handled by this algorithm (|U|)
	 */
	public RandomSubsetSums(float epsilon, float delta, int maxValue){
		super(epsilon, delta);	
		this.maxValue = maxValue;
		
		buckets = new CopyOnWriteArrayList<Bucket>();
		addNewBucket();
	}
		
	/**
	 * create a list of dyadic intervals that will, added together, describe the rank
	 * 
	 * @param rank
	 */
	public LinkedList<Interval> collectNeededIntervalls(int rank){
		LinkedList<Interval> intervals = new LinkedList<Interval>();
		int log2 = 0;
		int chunk = 0;
		int lowerBound = 0;
		int upperBound = 0;
		
		if(rank == 0){
			intervals.add(new Interval(0, 0));			
		}
		else {
			rank++; // we need to count the 0 extra
		
			while(rank > 0){
				log2 = (int) (Math.log10((double)rank) / Math.log10(2.0));
				chunk = (int)Math.pow(2.0d, (double)log2);
				upperBound = lowerBound + chunk - 1;
				intervals.add(new Interval(lowerBound, upperBound));
				lowerBound = upperBound + 1;
				rank -= chunk;
			}
		}
		return intervals;
	}
	
	/**
	 * returns the sum of all {@link Bucket}  estimations
	 * 
	 * @return the sum of all {@link Bucket}  estimations
	 */
	private int overallBucketCount(){
		int count = 0;
		for(Bucket bucket : buckets){
			count += bucket.getElementCount();
		}
		return count;
	}

	/**
	 * This method deploys the rss way to find a quantile. Its based on
	 * finding the sums of different intervals. To have a correct value
	 * we must consider all {@link Bucket} in the sliding window.
	 */
	@Override
	public SelectiveDescriptionModel<Double, Double> getModel() {
		return new SelectiveDescriptionModel<Double, Double>() {
			private static final long serialVersionUID = 5560769753329440202L;

			@Override
			public Double describe(Double phi) {
				int overallBucketCount = overallBucketCount();
				int wantedRank = (int)((float)overallBucketCount * phi.floatValue() - (float)overallBucketCount * epsilon);

				// this loop creates dyadic intervals 0..i and lets all buckets evaluate them.
				// then it tests if we have reatched the wanted rank with our sum.
				for(int i = 0; i < maxValue;i++){
					LinkedList<Interval> intervals = RandomSubsetSums.this.collectNeededIntervalls(i);			
					double intervalSum = 0;
					for(Bucket bucket : new LinkedList<Bucket>(buckets)) {
						intervalSum += Math.abs(bucket.estimateIntervals(intervals));
					}
					if(intervalSum > wantedRank){
						return (double)i;
					}
				}

				return (double)RandomSubsetSums.CANT_ESTIMATE;
			}
		};
	}
		
	/**
	 * Take the item and put it into the rss data structure
	 */
	@Override
	public void learn(Double item) {		
		newestBucket.process(Math.ceil(item));
		if(newestBucket.IsFull()){			
			addNewBucket();
		}		
		deleteExcessiveBuckets();
	}
	
	/**
	 * create a new {@link Bucket} 
	 */
	private void addNewBucket(){
		Bucket newBucket = new Bucket(epsilon, delta, maxValue); 
		buckets.add(newBucket);	
		newestBucket = newBucket;
	}

	/**
	 * delete oldest {@link Bucket}  while we have too many of them.
	 */
	private void deleteExcessiveBuckets(){
		while(buckets.size() > MAX_BUCKET_COUNT){			
			buckets.remove(0);
		}
	}
	 
	public void setElementsPerBucket(int newCount){
		ELEMENTS_PER_BUCKET = newCount;
	}
	
	public int getElementsPerBucket(){
	    return ELEMENTS_PER_BUCKET;
	}
	
	public void setMaxBucketCount(int newCount){
		MAX_BUCKET_COUNT = newCount;
		deleteExcessiveBuckets();
	}
	
	public int getMaxBucketCount(){
		return MAX_BUCKET_COUNT;
	}
	/*
	 * test function
	 * 
	 */
	public static void main(String args[]){    // <------ need since still in development
		
		for(int k = 0; k < 5; k++){
	//	RandomSubsetSums rss = new RandomSubsetSums(0.2f, 0.8f, 20);
	//	ExactQuantiles exact = new ExactQuantiles();
		WindowSketchQuantiles wsq = new WindowSketchQuantiles(0.2f); 
		
		
		
		Random random = new Random();
		
		System.out.println("work work work...");
		long startTime = System.currentTimeMillis();
			for(int i = 0; i < 1000; i++){
				int nextElement = random.nextInt(10) + 5;	// generate pw-length between 5 and 15		
			//	rss.learn((double)nextElement);
			//	exact.learn((double)nextElement);
				wsq.learn((double)nextElement);
			}
		long endTime = System.currentTimeMillis(); 
	
		
		//System.out.println("rss quantile = " + rss.getModel().describe(0.5) );
		//System.out.println("exact quantile = " + exact.getQuantile(0.5f));
		System.out.println("WindowSketchQuantiles = " + wsq.getModel().describe(0.5));
		System.out.println( " time " +  (endTime - startTime));
		}
		System.out.println("done");
	}
}
