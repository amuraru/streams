package stream.quantiles;

import stream.quantiles.rss.RandomSubsetSums;

public class QuantileFactory {
	
	private QuantileFactory(){
		
	}

	public static RandomSubsetSums createRSS(double epsilon, double delta,int maxValue, int windowSize, int bucketCount){
		return new RandomSubsetSums((float)epsilon, (float)delta, maxValue);
	}
	
	public static ExactQuantiles createExactQuantiles(){
		return new ExactQuantiles();
	}
	
	public static EnsembleQuantiles createEnsembleQuantiles(double epsilon){
		return new EnsembleQuantiles((float)epsilon);
	}
	
	public static SimpleQuantiles createSimpleQuantileEstimator(double epsilon){
		return new SimpleQuantiles((float)epsilon);
	}
	
	public static 	WindowSketchQuantiles createWindowSketchQuantiles(double epsilon){
		return new 	WindowSketchQuantiles((float)epsilon);
	}

        public static GKQuantiles createGKQuantiles(double epsilon) {
            return new GKQuantiles((float)epsilon);
        }
        public static SumQuantiles createSumQuantiles(int windowSize,int bucketCount) {
            return new SumQuantiles(windowSize, bucketCount);
        }
}
