package stream.counter.hashing;

import java.io.Serializable;
import java.util.Random;

/**
 * <p></p>
 *
 * @author Marcin Skirzynski
 */
public class SimpleHashFactory<T> implements HashFunctionFactory<T>{

	/** The unique class ID  */
	private static final long serialVersionUID = 1893281035106218246L;
	private Random random = new Random();
	@Override
	public HashFunction<T> build(final long domain) {
		return new SimpleHash(domain);
	}

	
	public class SimpleHash implements HashFunction<T>, Serializable {
		
		/** The unique class ID */
		private static final long serialVersionUID = -946774756839033767L;
		private long domain;
		
		public SimpleHash(long domain) {
			this.domain = domain;
		}

		@Override
		public long computeHash(T x) {
			return Math.abs((x.hashCode()*random.nextInt())%domain);
		}
	}
}
