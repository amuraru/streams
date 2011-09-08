/**
 * 
 */
package stream.data.vector;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class SparseVectorTest {
    
    static Logger log = LoggerFactory.getLogger( SparseVectorTest.class );

	/**
	 * Test method for {@link stream.data.vector.SparseVector#scale(double)}.
	 */
	@Test
	public void testScale() {
		
		int[] indexes = new int[]{ 0, 10, 124 };
		double[] vals = new double[]{ 9.1d, 1.4d, 2.1d };
		
		SparseVector vec = new SparseVector( indexes, vals, -1 );
		log.info( "vector = " + vec );
		double fac = 2.0d;
		vec.scale( fac );
		log.info( fac + " * vector = " + vec );
		
		for( int i = 0; i < indexes.length; i++ ){
			Assert.assertEquals( 2.0 * vals[i], vec.get( indexes[i] ) );
		}
	}

	/**
	 * Test method for {@link stream.data.vector.SparseVector#add(double, stream.data.vector.SparseVector)}.
	 */
	@Test
	public void testAdd() {
		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };

		int[] idx2= new int[]{ 0, 11, 124 };
		double[] val2 = new double[]{ 0.9d, 2.4d, 2.3d };

		SparseVector v1 = new SparseVector( idx1, val1, -1 );
		SparseVector v2 = new SparseVector( idx2, val2, -1 );
		
		log.info( "v1 = " + v1 );
		log.info( "v2 = " + v2 );
		
		SparseVector sumv = v1.add( 1.0d, v2 );
		log.info( "v1 + v2 = " + sumv );

		Assert.assertEquals( 10.0, sumv.get( 0 ) );
		Assert.assertEquals( 1.4, sumv.get( 10 ) );
		Assert.assertEquals( 2.4, sumv.get( 11 ) );
		Assert.assertEquals( 4.4, sumv.get( 124 ) );
	}

	/**
	 * Test method for {@link stream.data.vector.SparseVector#snorm()}.
	 */
	@Test
	public void testSnorm() {
		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };

		SparseVector v1 = new SparseVector( idx1, val1, -1 );
		double e1 = 0.0d;
		for( int i = 0; i < val1.length; i++ ){
			e1 += val1[i] * val1[i];
		}
		Assert.assertEquals( e1, v1.snorm() );
	}

	/**
	 * Test method for {@link stream.data.vector.SparseVector#size()}.
	 */
	@Test
	public void testSize() {
		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };

		SparseVector v1 = new SparseVector( idx1, val1, -1 );
		Assert.assertEquals( 3, v1.size() );
		log.info( "Checking size of vector " + v1 );
		log.info( "Size is: " + v1.size() );
	}

	@Test
	public void testInnerProduct2(){

		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };

		SparseVector v1 = new SparseVector( idx1, val1, -1 );

		double snorm = v1.snorm();
		double selfProd = v1.innerProduct( v1 );
		
		Assert.assertEquals( snorm, selfProd );
	}

	
	@Test
	public void testInnerProduct(){

		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 10.0d, 2.0d, 3.0d };


		int[] idx2= new int[]{ 0, 11, 124 };
		double[] val2 = new double[]{ 0.9d, 2.4d, 2.3d };

		SparseVector v1 = new SparseVector( idx1, val1, -1 );
		SparseVector v2 = new SparseVector( idx2, val2, -1 );

		double prod = v1.innerProduct( v2 );
		Assert.assertEquals( 9.0d + 6.9d, prod, 0.0000001 );
	}
}