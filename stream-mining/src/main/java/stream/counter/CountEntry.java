package stream.counter;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Standard data structure for a single item in the context of a counting algorithm.</p>
 *
 * <p>Contains fields for the item itself and its frequency.</p>
 *
 * @author Benedikt Kulmann, office@kulmann.biz
 */
public class CountEntry<T> implements Serializable, Cloneable {

    private transient static final Logger logger = LoggerFactory.getLogger(CountEntry.class);

    private static final long serialVersionUID = 1L;

    /**
     * The item this {@link CountEntry} is associated with.
     */
    public T item;

    /**
     * The frequency of this {@link CountEntry}s item within a counting algorithm.
     */
    public long frequency;

    /**
     * <p>Constructs a new instance of {@link CountEntry}.</p>
     *
     * @param item The item this {@link CountEntry} represents
     * @param frequency An initial count frequency. For the default initial frequency
     * use {@link #CountEntry(java.lang.Object)}
     */
    public CountEntry(T item, long frequency) {
        this.item = item;
        this.frequency = frequency;
    }

    /**
     * <p>Constructs a new instance of {@link CountEntry}.</p>
     *
     * @param item The item this {@link CountEntry} represents
     */
    public CountEntry(T item) {
        this(item, 0L);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CountEntry[item=" + item + ", freq=" + frequency + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountEntry<T> clone() {
        try {
            @SuppressWarnings("unchecked")
            CountEntry<T> clone = (CountEntry<T>)super.clone();
            return clone;
        } catch(CloneNotSupportedException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
