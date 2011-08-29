package stream.counter;

/**
 * <p>Factory for the creation of instances of the different count algorithms.</p>
 *
 * @author Benedikt Kulmann
 * @see CountSketch
 * @see CountMinSketch
 * @see LossyCounting
 * @see RealCounting
 * @see StickySampling
 */
public class CounterFactory {

    private CounterFactory() {
    }

    public static <T> SpaceSaving<T> createInstanceSpaceSaving(int counters, double minsupport, double maxError){
    	return new SpaceSaving<T>(counters, minsupport, maxError);
    }
    /**
     * <p>Returns a new instance of {@link CountSketch}.</p>
     *
     * @param <T>
     * @param domain The (estim.) domain, i.e. how many different items are expected
     * @param numberOfHashFunctions The number of hashfunctions which determine a bucket
     * @param numberOfBuckets The number of buckets where a counter will be maintained
     * @param k parameter for the top-k variant. If you want to disable
     * the top-k overhead, than set k to 0 or lower
     * @return A new instance of {@link CountSketch}
     */
    public static <T> CountSketch<T> createInstanceCountSketch(int domain, int numberOfHashFunctions, int numberOfBuckets, int k) {
        return new CountSketch<T>(domain, numberOfHashFunctions, numberOfBuckets, k);
    }

    /**
     * <p>Returns a new instance of {@link CountMinSketch}.</p>
     *
     * @param <T>
     * @param domain The (estim.) domain, i.e. how many different items are expected
     * @param numberOfHashFunctions The number of hashfunctions which determine a bucket
     * @param numberOfBuckets The number of buckets where a counter will be maintained
     * @param k parameter for the top-k variant. If you want to disable
     * the top-k overhead, than set k to 0 or lower
     * @return A new instance of {@link CountMinSketch}
     */
    public static <T> CountMinSketch<T> createInstanceCountMinSketch(int domain, int numberOfHashFunctions, int numberOfBuckets, int k) {
        return new CountMinSketch<T>(domain, numberOfHashFunctions, numberOfBuckets, k);
    }

    /**
     * <p>Returns a new instance of {@link LossyCounting}.</p>
     *
     * @param <T>
     * @param maxError the maximum error bound
     * @return A new instance of {@link LossyCounting}
     */
    public static <T> LossyCounting<T> createInstanceLossyCounting(double maxError) {
        return new LossyCounting<T>(maxError);
    }

    /**
     * <p>Returns a new instance of {@link RealCounting}.</p>
     *
     * @param <T>
     * @param minSupport the min support for frequent item queries
     * @param k the k for top k concerns
     * @return A new instance of {@link RealCounting}
     */
    public static <T> RealCounting<T> createInstanceRealCounting(double minSupport, int k) {
        return new RealCounting<T>(minSupport, k);
    }

    /**
     * <p>Returns a new instance of {@link StickySampling}.</p>
     * 
     * @param <T>
     * @param minSupport the min support frequent item queries
     * @param maxError the maximum error bound
     * @param maxFailure the maximum failure rate
     * @return A new instance of {@link StickySampling}
     */
    public static <T> StickySampling<T> createInstanceStickySampling(double minSupport, double maxError, double maxFailure) {
        return new StickySampling<T>(minSupport, maxError, maxFailure);
    }

}
