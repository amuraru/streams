package stream.quantiles;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import stream.model.SelectiveDescriptionModel;



public class SumQuantiles  implements QuantileLearner {
 	private static final long serialVersionUID = 7331604622548281844L;

 	private int slidingWindowSize = 5000;
	private int maxBucketCount = 10;
	private int elementPerBucket  = slidingWindowSize / maxBucketCount;
	private int biggestSeenElement = 0;
	
	
 	Bucket newestBucket = null;
 	List<Bucket> buckets;
 
 	public SumQuantiles(int slidingWindowSize, int bucketCount){
 		this.slidingWindowSize = slidingWindowSize;
 		this.maxBucketCount = bucketCount;
 		elementPerBucket  = slidingWindowSize / maxBucketCount;
 		buckets = new CopyOnWriteArrayList<Bucket>();
 		addNewBucket();
 	}
 	
 	public void init(){
 	}
 	
 	
 	private void addNewBucket(){
 		Bucket newBucket = new Bucket(); 
		buckets.add(newBucket);	
		newestBucket = newBucket;
		deleteExcessiveBuckets();
		//System.out.println("new");
 	}
	/**
	 * delete oldest {@link Bucket}  while we have too many of them.
	 */
	private void deleteExcessiveBuckets(){
		while(buckets.size() > maxBucketCount){			
			buckets.remove(0);			
		}
	}
	@Override
	public SelectiveDescriptionModel<Double, Double> getModel() {
		return new SelectiveDescriptionModel<Double, Double>() {
			private static final long serialVersionUID = -6028955231084094144L;

			@Override
			public Double describe(Double phi) {
				return getQuantile(phi);
			}
		};
	}
	
	public Double getQuantile(double phi) {
		int overallElementCount = 0;
		
		 for(Bucket bucket : buckets){
			 overallElementCount += bucket.getElementCount();
		 }
		
		int wantedRank = (int)((double)overallElementCount * phi);
		int sum = 0;
		
		//System.out.println("--------------------------------------" );
		//System.out.println("ElementCount " + overallElementCount);
		//System.out.println("wantedRank " + wantedRank);
		
		for(int i = 0; i < biggestSeenElement; i++){
			long predict = getAllBucketPrediction(i);
			sum += predict;
//			System.out.println(sum);
			if(sum >= wantedRank){				
				return (double)i;
			}		
		}
		return 0.0;		
	}
	
	private int getAllBucketPrediction(int item )
	{
		int prediction = 0;
		for(Bucket bucket : buckets){
			prediction += bucket.predict(item);
		}
		return prediction;
	}
	
	public void printBuckets(){
		 
		for(Bucket bucket : buckets){
			System.out.println(bucket);
		}
	}
	
	/**
	 * @see edu.udo.cs.pg542.util.DataStreamProcessor#process(java.lang.Object)
	 */
	@Override
	public void learn(Double item) {
		biggestSeenElement = Math.max(biggestSeenElement, item.intValue());
		 newestBucket.learn(item);
		 if(newestBucket.isFull()){
			 addNewBucket();
		 }
	}
	
	private class Bucket implements Serializable{
		 

		/**
		 * 
		 */
		private static final long serialVersionUID = -2211156505869843563L;

		int elementCount = 0;
		
	 	Map<String,Integer> counterMap ;
	 	
		public Bucket(){
			counterMap = new ConcurrentHashMap<String, Integer>();			 
		}
		
		public long predict(int item){			 
			String asString = ((Integer)item).toString();
			if(counterMap.containsKey(asString))
			{
				
				return counterMap.get(asString);
			}
			return 0;			 
		}
		
		public void learn(Double item) {
			int value = item.intValue();
			String asString = ((Integer)value).toString();
			if(counterMap.containsKey(asString))
			{
				int counter = counterMap.get(asString);
				counter++;
				counterMap.put(asString, counter);				 
			}
			else
			{
				counterMap.put(asString,1);
			}
			elementCount++;
		}
		
		public int getElementCount(){
			return elementCount;
		}
		
		public boolean isFull(){
			return elementCount >= elementPerBucket;
		}
	 
		@Override
		public String toString() {
			System.out.println("--------------------------------------" );
			String out = "Bucket: \n";
			 for(String key : counterMap.keySet()){
				 out = out + key + "  " +counterMap.get(key) + "\n"; 
			 }
			return out;
		}
	}
	
	public static void main(String args[]){    // <------ need since still in development
		SumQuantiles caq = new SumQuantiles(5000, 10);
		ExactQuantiles ex = new ExactQuantiles();
		double phi = 0.5;
		Random random = new Random();
		
		 
		
		for(int i = 0; i < 10; i++){
			int nextElement = random.nextInt(10000);			
			/*if((i % 1000) == 1)
			{				
				double sumResult = caq.getModel().describe(phi);
				double exactResult = ex.getModel().describe(phi);
				System.out.println("SumQuantiles   " + sumResult);
				System.out.println("ExactQuantiles " + exactResult);
				System.out.println("MSE            " + (sumResult - exactResult) * (sumResult - exactResult));
				System.out.println("done ?!" + i); 
							
			}*/
			caq.learn((double)nextElement);
			ex.learn((double)nextElement);
			
		//	System.out.println(nextElement);
			 
		}
		double sumResult = caq.getModel().describe(phi);
		double exactResult = ex.getModel().describe(phi);
		System.out.println("SumQuantiles   " + sumResult);
		System.out.println("ExactQuantiles " + exactResult);
		System.out.println("MSE            " + (sumResult - exactResult) * (sumResult - exactResult));
		caq.printBuckets();

		System.out.println("done");
	}
}
