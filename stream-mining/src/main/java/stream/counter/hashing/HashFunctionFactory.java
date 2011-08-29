package stream.counter.hashing;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @author Marcin Skirzynski
 */
public interface HashFunctionFactory<T> extends Serializable {

	public HashFunction<T> build( long domain );

}
