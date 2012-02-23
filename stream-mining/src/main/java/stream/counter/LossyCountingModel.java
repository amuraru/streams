package stream.counter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.model.Model;

/**
 * <p>
 * {@link Model}-part of the implementation of the Lossy Counting algorithm described in the paper
 * "Approximate Frequency Counts over Data Streams" written by 'Rajeev Motwani' and
 * 'Gurmeet Singh Manku'.
 * </p>
 *
 * @author Marcin Skirzynski (main work), Benedikt Kulmann (modifications)
 * @see LossyCounting
 *
 * @param <T>
 */
public class LossyCountingModel<T extends Serializable> implements DynamicFrequentItemModel<T> {

    private transient static final Logger logger = LoggerFactory.getLogger(LossyCountingModel.class);
    
    private static final long serialVersionUID = 1L;

    /**
     * The data structures which holds all
     * counting information.
     */
    private Map<T, CountEntryWithMaxError<T>> dataStructure;

    /**
     * The total count of all counted elements
     * in the stream so far.
     */
    private long elementsCounted;

    /**
     * The maximum error set be the user at the
     * beginning.
     */
    private double maxError;

    public LossyCountingModel(double maxError) {
        this.elementsCounted = 0;
        this.maxError = maxError;
        this.dataStructure = new ConcurrentHashMap<T, CountEntryWithMaxError<T>>();
    }

    public boolean containsItem(T item) {
        return dataStructure.containsKey(item);
    }

    void incrementCount(T item) {
        dataStructure.get(item).frequency++;
        elementsCounted++;
    }

    void insertNewItem(T item, long initialFrequency, long maxError) {
	dataStructure.put(item, new CountEntryWithMaxError<T>(item, initialFrequency, maxError));
        elementsCounted++;
    }

    Map<T, CountEntryWithMaxError<T>> getDataStructure() {
        return dataStructure;
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
    public Collection<T> getFrequentItems(double minSupport) {
        if (!(maxError < minSupport)) {
            logger.warn("LossyCounting strongly recommends that the maximum error is much lower than the min-support; currently set: error={}, min-support={}", maxError, minSupport);
        }
        Collection<T> result = new ArrayList<T>();
        for (T element : dataStructure.keySet()) {
            CountEntry<T> entry = dataStructure.get(element);
            if (entry.frequency >= (minSupport - maxError) * elementsCounted) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * <p>
     * Returns the estimated frequency of the given element.
     * </p>
     *
     * <p>
     * The LossyCounting algorithm compresses the internal data structure which means
     * that an element will be deleted if it doesn't emerge frequently enough.
     * That means that even when the element appeared in the stream
     * the estimated frequency can be 0.
     * </p>
     *
     * @param item the item for which the estimated frequency will be returned
     * @return the estimated frequency of the given item
     */
    @Override
    public Long predict(T item) {
        if (dataStructure.containsKey(item)) {
            return dataStructure.get(item).frequency;
        }
        return 0L;
    }

    @Override
    public Set<T> keySet() {
        return dataStructure.keySet();
    }

	/* (non-Javadoc)
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
	}
	 */
}