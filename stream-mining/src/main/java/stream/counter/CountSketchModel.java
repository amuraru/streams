package stream.counter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import stream.counter.hashing.HashFunction;
import stream.counter.hashing.HashFunctionFactory;
import stream.model.Model;

/**
 * <p>
 * {@link Model}-part of the implementation of the CountSketch algorithm from the paper
 * 'Finding frequent items in data streams' written by
 * 'Charikar, M., Chen, K., and Farach-colton, M. (2002)'.
 * </p>
 *
 * @author Marcin Skirzynski (main work), Benedikt Kulmann (modifications)
 * @see CountSketch
 *
 * @param <T>
 */
public class CountSketchModel<T extends Serializable> implements StaticTopKModel<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Total number of occurences of all elements counted so far.
     */
    protected long elementsCounted;

    /**
     * <p>
     * Data structure to estimate the frequency.
     * </p>
     */
    protected int[][] data;

    /**
     * <p>
     * As CountSketch also provides top-k estimation a parameter k can be provided.
     * If <= 0 top-k overhead is disabled.
     * </p>
     */
    protected int k;

    /**
     * <p>
     * Map for the top-k-algorithm.
     * </p>
     */
    protected Map<T, CountEntry<T>> topItems;

	/**
	 * <p>The hash function which will be used.</p>
	 */
	protected Class<? extends HashFunction<?>> functionClass;

    /*
     * Have to operate with collections since we cannot
     * instantiate generic arrays
     */
    /**
     * <p>
     * Hashfunctions which determine the bucket to add the
     * s-hashfunctions.
     * </p>
     */
    protected List<HashFunction<T>> h;

    /**
     * Hashfunctions which determine the value which will be added.
     */
    protected List<HashFunction<T>> s;

    public CountSketchModel(int domain, int nrOfHashFunctions, int nrOfbuckets, int k, HashFunctionFactory<T> hashFactory) {

        this.k = k;
        this.elementsCounted = 0L;
        this.topItems = new ConcurrentHashMap<T, CountEntry<T>>();

        // Initialize data structure
        data = new int[nrOfHashFunctions][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new int[nrOfbuckets];
        }

        initializeHashes(domain, nrOfHashFunctions, nrOfbuckets, hashFactory);
    }

    /**
     * <p>
     * Initialize all necessary hash functions.
     * </p>
     *
     * @param domain				the (estim.) domain, i.e. how many different items are expected
     * @param nrOfHashFunctions		the number of hashfunctions which determine a bucket
     * @param nrOfbuckets			the number of buckets where a counter will be maintained
     */
    protected void initializeHashes(int domain, int nrOfHashFunctions, int nrOfbuckets, HashFunctionFactory<T> factory) {
        h = new ArrayList<HashFunction<T>>();
        s = new ArrayList<HashFunction<T>>();

        for (int i = 0; i < nrOfHashFunctions; i++) {
            h.add(factory.build(nrOfbuckets));
            s.add(factory.build(2));
//            h.add(new TwoUniversalHashFunction<T>(domain, nrOfbuckets));
//            s.add(=new TwoUniversalHashFunction<T>(domain, 2));
        }
    }

    /**
     * <p>
     * Estimates the frequency of the provided item.
     * </p>
     *
     * @param	item	the item which frequency shall be estimated
     * @return	the estimated frequency of the item
     */
    public long estimateFrequency(T item) {

        List<Integer> values = new ArrayList<Integer>();
        for (int i = 0; i < h.size(); i++) {
            int hi = (int) h.get(i).computeHash(item);
            values.add(data[i][hi]);
        }

        Collections.sort(values);

        if (values.size() % 2 == 1) {
            return values.get((values.size() + 1) / 2 - 1);
        } else {
            double lower = values.get(values.size() / 2 - 1);
            double upper = values.get(values.size() / 2);

            return (long) ((lower + upper) / 2.0);
        }

    }

    /**
     * <p>
     * Returns the top-k items which were
     * counted so far.
     * </p>
     *
     * <p>
     * If k was set to 0 or lower, an empty
     * Collection will be returned.
     * </p>
     *
     * @return	a collection of the top-k items
     */
    @Override
    public Collection<T> getTopK() {
        if (k <= 0) {
            return new ArrayList<T>();
        }
        return topItems.keySet();
    }

    boolean isTopItem(T item) {
        return topItems.containsKey(item);
    }

    boolean notYetKItems() {
        return topItems.size() <= k;
    }

    void incrementCount(T item) {
        topItems.get(item).frequency++;
        elementsCounted++;
    }

    void insertTopItem(T item, long initialFrequncy) {
        topItems.put(item, new CountEntry<T>(item, initialFrequncy));
        elementsCounted += initialFrequncy;
    }

    void removeTopItem(T item) {
        topItems.remove(item);
    }

    CountEntry<T> getItemWithLowestCount() {
        return Collections.min(topItems.values(), new Comparator<CountEntry<T>>() {

            @Override
            public int compare(CountEntry<T> o1, CountEntry<T> o2) {
                return new Long(o1.frequency).compareTo(o2.frequency);
            }
        });
    }

    /**
     * <p>
     * Updates the data structure with the given item.
     * </p>
     *
     * @param item	the item to insert into the data structure
     */
    boolean updateData(T item) {
        for (int i = 0; i < h.size(); i++) {
            int hi = (int) h.get(i).computeHash(item);
            int si = (int) s.get(i).computeHash(item);
            if (si == 0) {
                si = -1;
            }

            data[i][hi] += si;
        }

        return k <= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getTotalCount() {
        return elementsCounted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<T> keySet() {
        return topItems.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long predict(T item) {
        if (isTopItem(item)) {
            return topItems.get(item).frequency;
        } else {
            return estimateFrequency(item);
        }
    }

	/**
	 * @see stream.counter.CountModel#getCount(java.io.Serializable)
	 */
	@Override
	public Long getCount(T value) {
		return predict( value );
	}

	/* (non-Javadoc)
	 * @see stream.counter.CountModel#count(java.io.Serializable)
	@Override
	public void count(T value) {
		this.updateData( value );
	}
	 */
}
