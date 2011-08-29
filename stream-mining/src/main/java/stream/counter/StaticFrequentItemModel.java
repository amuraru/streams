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
 * {@link DynamicFrequentItemModel} interface the user can specify the
 * threshold on method invokation. In this version, the {@link StaticFrequentItemModel}
 * you have to specify the threshold on instantiation.
 * </p>
 *
 * @author Marcin Skirzynski, Benedikt Kulmann
 *
 * @param <T>	Generic class of the elements which should be counted
 */
public interface StaticFrequentItemModel<T> extends CountModel<T> {

    /**
     * <p>
     * Returns all elements counted in the stream which exceed the
     * threshold which has been defined at instantiation time.
     * </p>
     *
     * @return all elements which exceed the threshold
     */
    public Collection<T> getFrequentItems();
}