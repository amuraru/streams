/**
 * 
 */
package stream.data.vector;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.vector.Vector;

/**
 * @author chris
 *
 */
public class VectorTest {
    
    static Logger log = LoggerFactory.getLogger( VectorTest.class );

	/**
	 * Test method for {@link stream.data.vector.Vector#scale(double)}.
	 */
	@Test
	public void testScale() {
		
		int[] indexes = new int[]{ 0, 10, 124 };
		double[] vals = new double[]{ 9.1d, 1.4d, 2.1d };
		
		Vector vec = new Vector( indexes, vals );
		Vector vecDense = new Vector (vals);
		log.info( "vector = " + vec );
		log.info( " vector (dense) " + vecDense );
		double fac = 2.0d;
		vec.scale( fac );
		vecDense.scale( fac );
		log.info( fac + " * vector = " + vec );
		
		for( int i = 0; i < indexes.length; i++ ){
			Assert.assertEquals( 2.0 * vals[i], vec.get( indexes[i] ) );
			Assert.assertEquals( 2.0 * vals[i], vecDense.get( i ) );
		}
	}

	/**
	 * Test method for {@link stream.data.vector.Vector#add(double, stream.data.vector.Vector)}.
	 */
	@Test
	public void testAdd() {
		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };

		int[] idx2= new int[]{ 0, 11, 124 };
		double[] val2 = new double[]{ 0.9d, 2.4d, 2.3d };

		Vector v1 = new Vector( idx1, val1 );
		Vector v2 = new Vector( idx2, val2 );
		Vector v1Dense = new Vector ( idx1, val1, 125);
		Vector v2Dense = new Vector ( idx2, val2, 125);
		
		log.info( "v1 = " + v1 );
		log.info( "v2 = " + v2 );
		log.info( "v1 (dense) = " + v1Dense);
		log.info( "v2 (dense) = " + v2Dense);
		
		Vector sumv_ss = v1.add( 1.0d, v2 );
		Vector sumv_dd = v1Dense.add( 1.0d, v2Dense);
		log.info( "v1 + v2 = " + sumv_dd );
		log.info( "v1 + v2 = " + sumv_ss );

		Assert.assertEquals( 10.0, sumv_ss.get( 0 ) );
		Assert.assertEquals( 1.4, sumv_ss.get( 10 ) );
		Assert.assertEquals( 2.4, sumv_ss.get( 11 ) );
		Assert.assertEquals( 4.4, sumv_ss.get( 124 ) );
	
		v1 = new Vector( idx1, val1 );
		v1Dense = new Vector ( idx1, val1, 125);
		Vector sumv_sd = v1.add( 1.0d, v2Dense );
		Vector sumv_ds = v1Dense.add( 1.0d, v2);
		log.info( "v1 + v2 = " + sumv_sd );
		log.info( "v1 + v2 = " + sumv_ds );


	}

	/**
	 * Test method for {@link stream.data.vector.Vector#snorm()}.
	 */
	@Test
	public void testSnorm() {
		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };

		Vector v1 = new Vector( idx1, val1 );
		Vector v1Dense = new Vector ( idx1, val1, 125 );
		double e1 = 0.0d;
		for( int i = 0; i < val1.length; i++ ){
			e1 += val1[i] * val1[i];
		}
		Assert.assertEquals( e1, v1.snorm() );
		Assert.assertEquals( e1, v1Dense.snorm() );
	}

//	/**
//	 * Test method for {@link stream.data.vector.Vector#size()}.
//	 */
//	@Test
//	public void testSize() {
//		int[] idx1= new int[]{ 0, 10, 124 };
//		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };
//
//		Vector v1 = new Vector( idx1, val1 );
//		Assert.assertEquals( 3, v1.size() );
//		log.info( "Checking size of vector " + v1 );
//		log.info( "Size is: " + v1.size() );
//	}

	@Test
	public void testInnerProduct2(){

		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };

		Vector v1 = new Vector( idx1, val1 );
		Vector v1Dense = new Vector( idx1, val1, 125 );

		Assert.assertEquals( v1.snorm(), v1.innerProduct( v1 ) );
		Assert.assertEquals( v1Dense.snorm(), v1Dense.innerProduct(v1Dense) );
		Assert.assertEquals( v1.snorm(), v1.innerProduct(v1Dense) );
		Assert.assertEquals( v1Dense.snorm(), v1Dense.innerProduct(v1) );
	}

	
	@Test
	public void testInnerProduct(){

		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 10.0d, 2.0d, 3.0d };


		int[] idx2= new int[]{ 0, 11, 124 };
		double[] val2 = new double[]{ 0.9d, 2.4d, 2.3d };

		Vector v1 = new Vector( idx1, val1 );
		Vector v2 = new Vector( idx2, val2 );
		Vector v1Dense = new Vector( idx1, val1, 125 );
		Vector v2Dense = new Vector( idx2, val2, 125 );

		Assert.assertEquals( 9.0d + 6.9d, v1.innerProduct( v2 ), 0.0000001 );
		Assert.assertEquals( 9.0d + 6.9d, v1.innerProduct( v2Dense ), 0.0000001 );
		Assert.assertEquals( 9.0d + 6.9d, v1Dense.innerProduct( v2 ), 0.0000001 );
		Assert.assertEquals( 9.0d + 6.9d, v1Dense.innerProduct( v2Dense ), 0.0000001 );
	}
}