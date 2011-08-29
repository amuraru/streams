package stream.counter;

import java.util.Collection;

/**
 * <p>
 * Interface which extends the {@link CountModel} interface by an
 * output method. This output method is intended for all stream counting
 * algorithms, which return a collection of elements counted in the stream
 * which exceeded a given frequency threshold.
 * </p>
 *
 * <p>
 * There are two versions of the threshold stream counting interface. In the
 * {@link StaticFrequentItemModel} interface the user can specify the
 * threshold on object instantiation. In this version, the {@link DynamicFrequentItemModel}
 * you have to specify the threshold on method invokation.
 * </p>
 *
 * @author Marcin Skirzynski, Benedikt Kulmann
 *
 * @param <T>	Generic class of the elements which should be counted
 */
public interface DynamicFrequentItemModel<T> extends CountModel<T> {

    /**
     * <p>
     * Returns all elements counted in the stream which exceed the
     * specified threshold.
     * </p>
     *
     * @param minSupport frequency threshold for all returned elements. Has
     * 	to be between 0 and 1
     * @return all elements which exceed the threshold
     */
    public Collection<T> getFrequentItems(double minSupport);
}