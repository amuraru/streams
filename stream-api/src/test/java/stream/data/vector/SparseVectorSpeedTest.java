/**
 * 
 */
package stream.data.vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import org.junit.Test;

/**
 * @author chris
 *
 */
public class SparseVectorSpeedTest {

	static int NUMBER_OF_VECTORS = 1000;
	static List<SparseVector> samples = createRandomVectors( NUMBER_OF_VECTORS );
	
	protected static List<SparseVector> createRandomVectors( int num ){
		ArrayList<SparseVector> rnds = new ArrayList<SparseVector>( num );
		for( int i = 0; i < num; i++ ){
			rnds.add( createRandomVector() );
		}
		return rnds;
	}
	
	protected static SparseVector createRandomVector(){

		Random rnd = new Random();
		int size = rnd.nextInt( 1000 );
		
		TreeSet<Integer> indexes = new TreeSet<Integer>();
		
		for( int i = 0; i < size; i++ ){
			indexes.add( new Integer( rnd.nextInt( size ) ) );
		}

		int[] idx = new int[ indexes.size() ];
		double[] val = new double[ indexes.size() ];
		
		int k = 0;
		Iterator<Integer> it = indexes.iterator();
		while( it.hasNext() ){
			idx[k] = it.next();
			val[k] = rnd.nextDouble();
			k++;
		}
		
		return new SparseVector( idx, val, -1, false );
	}
	
	
	/**
	 * Test method for {@link stream.data.vector.SparseVector#add(double, stream.data.vector.SparseVector)}.
	 */
	@Test
	public void testAddDoubleSparseVector() {
		
		double sizes = 0.0d;
		for( SparseVector vec : samples ){
			sizes += vec.size();
		}
		System.out.println( "average vector-size is " + (sizes / samples.size()) );
		
		
		SparseVector sum = new SparseVector();
		long start = System.currentTimeMillis();

		for( SparseVector vec : samples ){
			sum = sum.add( 1.0d, vec );
		}
		
		long end = System.currentTimeMillis();
		System.out.println( "Sum is: " + sum );
		System.out.println( "Size of sum is: " + sum.size() + ", mem-size is: " + sum.memSize() );
		System.out.println( "Summing up " + samples.size() + " random vectors took " + (end-start) + "ms.");
	}
}
