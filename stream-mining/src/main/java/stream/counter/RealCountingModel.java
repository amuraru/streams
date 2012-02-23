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

import stream.model.Model;

/**
 * <p>
 * {@link Model}-part of a naive counter implementation. Simply contains a counter
 * for each element which will be incremented in a deterministic way within the learn
 * method. Of course the purpose of this implementation is not a "live environment". Instead
 * it is intended to be used as "the truth" for evaluation intents.
 * </p>
 *
 * @author Benedikt Kulmann, office@kulmann.biz
 */
public class RealCountingModel<T extends Serializable> implements DynamicFrequentItemModel<T>, StaticFrequentItemModel<T>, DynamicTopKModel<T>, StaticTopKModel<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Top-K parameter for the invokation of {@link #getTopK()}.
     */
    private int k;

    /**
     * Threshold value for the invokation of {@link #getFrequentItems()}.
     */
    private double minSupport;

    /**
     * Total number of occurences of all elements so far.
     */
    private long elementsCounted;

    /**
     * Internal data structure for the count frequencies of each element.
     */
    private Map<T, CountEntry<T>> dataStructure;

    public RealCountingModel() {
        this(0.0);
    }

    public RealCountingModel(double minSupport) {
        this(minSupport, 0);
    }

    public RealCountingModel(int k) {
        this(0.0, k);
    }

    public RealCountingModel(double minSupport, int k) {
        this.minSupport = minSupport;
        this.k = k;
        elementsCounted = 0;
        dataStructure = new ConcurrentHashMap<T, CountEntry<T>>();
    }

    /**
     * Returns whether the internal data structure already contains a counter for
     * the provided item.
     *
     * @param item The item for which the existence of a counter is in question.
     * @return <code>true</code> if a counter for the provided item already exists, false otherwise.
     */
    boolean containsItem(T item) {
        return dataStructure.containsKey(item);
    }

    /**
     * Increments the counter of the provided item by 1.
     *
     * @param item Ttem in question.
     */
    void incrementCount(T item) {
        dataStructure.get(item).frequency++;
        elementsCounted++;
    }

    /**
     * Creates a counter for the provided item and sets its initial frequency to 1.
     *
     * @param item The item to insert into the internal data structure.
     */
    void insertItem(T item) {
        dataStructure.put(item, new CountEntry<T>(item, 1));
        elementsCounted++;
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
    public Long predict(T item) {
        if (dataStructure.containsKey(item)) {
            return dataStructure.get(item).frequency;
        } else {
            return 0L;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> getFrequentItems() {
        return getFrequentItems(minSupport);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> getFrequentItems(double minSupport) {
        Collection<T> frequentItems = new ArrayList<T>();

        for (CountEntry<T> entry : dataStructure.values()) {
            if (isFrequent(entry.frequency, minSupport)) {
                frequentItems.add(entry.item);
            }
        }

        return frequentItems;
    }

    /**
     * Determines whether a frequency is currently (i.e. in relation to the current total number
     * of elements) said to be frequent given a specific threshold.
     *
     * @param frequency The frequency in question
     * @param minSupport The threshold for determining whether a frequency is deemed to be frequent
     * @return
     */
    private boolean isFrequent(long frequency, double minSupport) {
        return frequency >= minSupport * elementsCounted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> getTopK(int k) {
        return getTopK(k);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> getTopK() {
        List<CountEntry<T>> items = new ArrayList<CountEntry<T>>(dataStructure.values());
        Collections.sort(items, new Comparator<CountEntry<T>>() {
            @Override
            public int compare(CountEntry<T> o1, CountEntry<T> o2) {
                return new Long(o1.frequency).compareTo(new Long(o2.frequency));
            }
        });

        Collection<T> topKItems = new ArrayList<T>();
        for (CountEntry<T> entry : items.subList(items.size() - k, items.size() - 1)) {
            topKItems.add(entry.item);
        }

        return topKItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<T> keySet() {
        return dataStructure.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RealCountingModel[");
        for(T key : keySet()) {
            sb.append(key).append(" ").append(dataStructure.get(key)).append(";");
        }
        sb.append("]");
        return sb.toString();
    }

	/**
	 * @see stream.counter.CountModel#getCount(java.io.Serializable)
	 */
	@Override
	public Long getCount(T value) {
		return this.predict( value );
	}
}