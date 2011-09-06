/**
 * 
 */
package stream.data.vector;

import static org.junit.Assert.fail;

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

	
	protected SparseVector createRandomVector(){

		Random rnd = new Random();
		int size = rnd.nextInt( 10000 );
		
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
		
		int runs = 1000;
		List<SparseVector> set = new ArrayList<SparseVector>( runs );
		double sizes = 0.0d;
		
		for( int i = 0; i < runs; i++ ){
			SparseVector vec = createRandomVector();
			set.add( vec );
			sizes += vec.size();
		}
		System.out.println( "average vector-size is " + (sizes / runs) );
		
		
		SparseVector sum = new SparseVector();
		long start = System.currentTimeMillis();

		for( SparseVector vec : set ){
			sum = sum.add( 1.0d, vec );
		}
		
		long end = System.currentTimeMillis();
		System.out.println( "Sum is: " + sum );
		System.out.println( "Summing up " + runs + " random vectors took " + (end-start) + "ms.");
	}

	/**
	 * Test method for {@link stream.data.vector.SparseVector#innerProduct(stream.data.vector.SparseVector)}.
	 */
	@Test
	public void testInnerProductSparseVector() {
		fail("Not yet implemented");
	}

}
