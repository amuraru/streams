package stream.counter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import stream.counter.hashing.HashFunction;
import stream.counter.hashing.SimpleHashFactory;
import stream.counter.hashing.TwoUniversalHashFunction;
import stream.model.Model;

/**
 * <p>
 * {@link Model}-part of the implementation of the CountMinSketch algorithm from the paper
 * 'An improved data stream summary: the count-min sketch and its
 * 	applications' written by
 * 'Cormode, G. and Muthukrishnan, S. (2003)'.<br />
 * </p>
 *
 * @author Marcin Skirzynski (main work), Benedikt Kulmann (modifications)
 *
 * @see CountMinSketch
 */
public class CountMinSketchModel<T extends Serializable> extends CountSketchModel<T> {

    private static final long serialVersionUID = 1L;

    public CountMinSketchModel(int domain, int nrOfHashFunctions, int nrOfbuckets, int k) {
        super(domain, nrOfHashFunctions, nrOfbuckets, k, new SimpleHashFactory<T>());
    }

    /**
     * <p>
     * We only need the h-hash functions since
     * the CountMinSketch algorithm always increments
     * the values in the data.
     * </p>
     *
     * @param domain				the (estim.) domain, i.e. how many different items are expected
     * @param nrOfHashFunctions		the number of hashfunctions which determine a bucket
     * @param nrOfbuckets			the number of buckets where a counter will be maintained
     */
    protected void initializeHashes(int domain, int nrOfHashFunctions, int nrOfbuckets) {
        h = new ArrayList<HashFunction<T>>();
        s = new ArrayList<HashFunction<T>>();

        for (int i = 0; i < nrOfHashFunctions; i++) {
            h.add(new TwoUniversalHashFunction<T>(domain, nrOfbuckets));
        }
    }

    /**
     * <p>
     * Updating the data. For each hashfunction the corresponding
     * bucket will be incremented by one.
     * </p>
     */
    @Override
    boolean updateData(T item) {
        for (int i = 0; i < h.size(); i++) {
            int hi = (int) h.get(i).computeHash(item);
            data[i][hi] += 1;
        }

        return k <= 0;
    }

    /**
     * <p>
     * Estimates the frequency by returning the
     * smallest frequency value for each hasfunction index.
     * </p>
     */
    @Override
    public long estimateFrequency(T item) {
        Collection<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < h.size(); i++) {
            int hi = (int) h.get(i).computeHash(item);
            values.add(data[i][hi]);
        }

        return Collections.min(values);
    }
}