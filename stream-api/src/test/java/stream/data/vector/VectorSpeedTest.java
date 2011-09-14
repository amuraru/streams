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

import stream.data.vector.Vector;

/**
 * @author chris
 *
 */
public class VectorSpeedTest {

	static int NUMBER_OF_VECTORS = 1000;
	static List<Vector> samples = createRandomVectors( NUMBER_OF_VECTORS );
	
	protected static List<Vector> createRandomVectors( int num ){
		ArrayList<Vector> rnds = new ArrayList<Vector>( num );
		for( int i = 0; i < num; i++ ){
			rnds.add( createRandomVector() );
		}
		return rnds;
	}
	
	protected static Vector createRandomVector(){

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
		
		return new Vector( idx, val, -1 );
	}
	
	
	/**
	 * Test method for {@link stream.data.vector.Vector#add(double, stream.data.vector.Vector)}.
	 */
	@Test
	public void testAddDoubleVector() {
		
		double sizes = 0.0d;
		for( Vector vec : samples ){
			sizes += vec.length();
		}
		System.out.println( "average vector-size is " + (sizes / samples.size()) );
		
		
		Vector sum = new Vector();
		long start = System.currentTimeMillis();

		for( Vector vec : samples ){
			sum = sum.add( 1.0d, vec );
		}
		
		long end = System.currentTimeMillis();
		System.out.println( "Sum is: " + sum );
		//System.out.println( "Size of sum is: " + sum.size() + ", mem-size is: " + sum.memSize() );
		System.out.println( "Summing up " + samples.size() + " random vectors took " + (end-start) + "ms.");
	}
}
