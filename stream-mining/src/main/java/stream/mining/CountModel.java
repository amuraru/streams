package stream.mining;

import java.io.Serializable;
import java.util.Set;

import stream.model.PredictionModel;

/**
 * <p>Extension of the {@link PredictionModel} interface to be able to provide
 * additional information which seems to be common for count algorithms.<br />
 * Currently this is the total number of elements counted so far, available through
 * {@link #getTotalCount()} and the "key set" the counting (so far) happened for,
 * available through {@link #keySet()}.
 *
 * @author Benedikt Kulmann, office@kulmann.biz
 */
public interface CountModel<T extends Serializable> extends PredictionModel<T,Long> {

    /**
     * Returns the total number of elements counted so far.
     * @return the total number of elements counted so far.
     */
    Long getTotalCount();

    /**
     * Returns the current "key set" of the counting algorithm which means the different
     * elements that have occurred so far.
     * @return The set of different elements which have occurred so far.
     */
    Set<T> keySet();
    
    
    public Long getCount( T value );
    
    public void count( T value );
}
