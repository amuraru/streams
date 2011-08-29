package stream.counter.hashing;

/**
 * <p></p>
 *
 * @author Marcin Skirzynski
 */
public class TwoUniversalHashFactory<T> implements HashFunctionFactory<T>{
	/** The unique class ID */
	private static final long serialVersionUID = -1933181492426760268L;

	@Override
	public HashFunction<T> build(long domain) {
		return null;//new TwoUniversalHashFunction<T>();
	}
}
