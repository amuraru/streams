package stream.counter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import stream.model.Model;

/**
 * {@link Model}-part of the implementation of the "Sticky Sampling" algorithm as described in the paper
 * "Approximate Frequency Counts over Data Streams" written by Gurmeet Singh Manku
 * and Rajeev Motwani
 *
 * @author Benedikt Kulmann
 * @see StickySampling
 */
public final class StickySamplingModel<T> implements StaticFrequentItemModel<T> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Threshold whether an element is frequent or not.</p>
     */
    private final double support;

    /**
     * <p>Epsilon around support.</p>
     *
     * <p><i>Rule of thumb: 10% of support</i></p>
     */
    private final double error;

    /**
     * <p>The data structure which holds all counting information.</p>
     */
    private final Map<T, CountEntry<T>> dataStructure;

    /**
     * The total count of all counted elements
     * in the stream so far.
     */
    private long elementsCounted;

    /**
     * Creates a new instance of the StickySamplingModel, used by {@link StickySampling}</p>
     *
     * @param support Threshold whether an element of the data structure is frequent. Has to be out of (0,1).
     * @param error An epsilon for the threshold. Has to be out of (0,1).
     */
    public StickySamplingModel(double support, double error) {
	if(support <= 0 || support >= 1) {
	    throw new IllegalArgumentException("Support has to be > 0 and < 1.");
	}
	if(error <= 0 || error >= 1) {
	    throw new IllegalArgumentException("Error has to be > 0 and < 1.");
	}

        this.support = support;
        this.error = error;
        this.elementsCounted = 0;
        this.dataStructure = new ConcurrentHashMap<T, CountEntry<T>>();
    }

    /**
     * <p>Removes the {@link CountEntry} associated with the provided item from the internal
     * data structure.</p>
     *
     * @param itemToRemove The item whose {@link CountEntry} shall be removed
     */
    void removeItem(T itemToRemove) {
        dataStructure.remove(itemToRemove);
    }

    /**
     * <p>Returns whether the internal data structure contains a counter for the provided item.</p>
     *
     * @param item The item in question
     * @return True if the internal data structure contains a counter for the provided item, false otherwise.
     */
    boolean containsItem(T item) {
        return dataStructure.containsKey(item);
    }

    /**
     * <p>Increment the count frequency of the provided item by 1.</p>
     *
     * @param item The item whose frequency shall be incremented by 1.
     */
    void incrementCount(T item) {
        dataStructure.get(item).frequency++;
        elementsCounted++;
    }

    /**
     * <p>Decrements the count frequency of the provided item by 1. Used within the
     * {@link StickySampling#adaptNewSamplingRate()} method of the algorithm.</p>
     *
     * @param item The item whose count frequency shall be decremented by 1.
     */
    void decrementCount(T item) {
        dataStructure.get(item).frequency--;
    }

    /**
     * <p>Returns whether the count frequency of the provided item corresponds to 0
     * (i.e. frequency == 0 or item doesn't exist within the internal data structure).</p>
     *
     * @param item The item in question.
     * @return true if the count frequency of the provided item corresponds to 0
     */
    boolean frequencyIsZero(T item) {
        return !dataStructure.containsKey(item) || dataStructure.get(item).frequency == 0;
    }

    /**
     * Inserts the provided item into the internal data structure with an initial count of 1.
     *
     * @param item The item which shall be inserted into the internal data structure
     */
    void insertEntry(T item) {
        dataStructure.put(item, new CountEntry<T>(item, 1));
        elementsCounted++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTotalCount() {
        return elementsCounted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> getFrequentItems() {
        Collection<T> frequentItems = new ArrayList<T>();

        for (CountEntry<T> entry : dataStructure.values()) {
            if (isFrequent(entry.frequency)) {
                frequentItems.add(entry.item);
            }
        }

        return frequentItems;
    }

    /**
     * <p>Returns whether the provided frequency is a frequent one in terms of sticky sampling.</p>
     *
     * @param frequency The frequency which shall be tested
     * @return True if the frequency would classify an item as frequent in terms of sticky sampling, false otherwise
     */
    public boolean isFrequent(long frequency) {
        return frequency >= (support - error) * elementsCounted;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Specifically this is the current count approximation for the provided item, or 0
     * if it doesn't exist within the internal data structure.</p>
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
    public Set<T> keySet() {
        return dataStructure.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("StickySamplingModel[");
        for (T key : keySet()) {
            sb.append(dataStructure.get(key)).append(";");
        }
        sb.append("]");
        return sb.toString();
    }
}
