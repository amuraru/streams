package stream.counter;

import stream.learner.Learner;

/**
 * <p>
 * {@link Learner}-part of the implementation of the CountMinSketch algorithm from the paper
 * 'An improved data stream summary: the count-min sketch and its
 * 	applications' written by 
 * 'Cormode, G. and Muthukrishnan, S. (2003)'.
 * </p>
 * 
 * @author Marcin Skirzynski (main work), Benedikt Kulmann (modifications)
 * @see CountSketch
 * @see CountMinSketchModel
 *
 * @param <T>
 */
public class CountMinSketch<T> extends CountSketch<T> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * Constructor of the CountMinSketch algorithm. This construction
     * can take quite a long time since the construction of the hashfunctions
     * is rather time-consuming.
     * </p>
     *
     * @param domain The (estim.) domain, i.e. how many different items are expected
     * @param numberOfHashFunctions The number of hashfunctions which determine a bucket
     * @param numberOfBuckets The number of buckets where a counter will be maintained
     * @param k parameter for the top-k variant. If you want to disable
     * the top-k overhead, than set k to 0 or lower
     */
    public CountMinSketch(int domain, int numberOfHashFunctions, int numberOfBuckets, int k) {
        model = new CountMinSketchModel<T>(domain, numberOfHashFunctions, numberOfBuckets, k);
    }

    /**
     * Same as {@link #CountMinSketch(int, int, int, int)} but with disabled top-k.
     */
    public CountMinSketch(int domain, int numberOfHashFunctions, int numberOfBuckets) {
        this(domain, numberOfHashFunctions, numberOfBuckets, 0);
    }
}
