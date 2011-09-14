/**
 * 
 */
package stream.learner.util;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import stream.data.vector.SparseVector;

import stream.optimization.GaussianFeatureMapping;

/**
 * @author chris
 *
 */
public class SparseVectorTest {

	/**
	 * Test method for {@link stream.optmization.GaussianFeatureMapping}
	 * @author Sangkyun Lee
	 */
	@Test
	public void testGaussian() {
		int nExample = 10;
		int dim = 100;
		double gamma = 0.001;
		Random rand = new Random();
		ArrayList<SparseVector> dataset = new ArrayList<SparseVector>();
		for(int i=0; i<nExample; ++i) {
			HashMap<Integer,Double> example = new HashMap<Integer,Double>();;
			for(int j=0; j<dim; ++j) {
				example.put(j, rand.nextDouble());
			}
			dataset.add(new SparseVector(example, -1, false));
		}
		
		GaussianFeatureMapping kernel = new GaussianFeatureMapping(0.001, dim);
		ArrayList<SparseVector> trans_dataset = new ArrayList<SparseVector>();
		for(SparseVector v : dataset) {
			trans_dataset.add( kernel.transform(v) );
		}
		
		double kernel_ori;
		double kernel_apx;
		for(int i=0; i<nExample; ++i) {
			for (int j=i; j<nExample; ++j) {
				SparseVector x1 = dataset.get(i);
				SparseVector x2 = dataset.get(j);
				SparseVector x1tmp = new SparseVector(x1);
				x1tmp.add(-1.0, x2);
				kernel_ori = Math.exp( -gamma *  x1tmp.snorm() );
				SparseVector t1 = trans_dataset.get(i);
				SparseVector t2 = trans_dataset.get(j);
				kernel_apx = t1.innerProduct(t2);
				System.out.println("|kernel - kernel(approx)| = " + Math.abs(kernel_ori - kernel_apx));
			}
		}
		
		
	}
	/**
	 * Test method for {@link stream.data.vector.SparseVector#scale(double)}.
	 */
	@Test
	public void testScale() {
		
		int[] indexes = new int[]{ 0, 10, 124 };
		double[] vals = new double[]{ 9.1d, 1.4d, 2.1d };
		HashMap<Integer,Double> pairs = new HashMap<Integer,Double>();
		for(int i=0; i<indexes.length; ++i) {
			pairs.put(indexes[i], vals[i]);
		}
		
		//SparseVector vec = new SparseVector( indexes, vals, -1 );
		SparseVector vec = new SparseVector( pairs, -1 );
		System.out.println( "vector = " + vec );
		double fac = 2.0d;
		vec.scale( fac );
		System.out.println( fac + " * vector = " + vec );
		
		for( int i = 0; i < indexes.length; i++ ){
			Assert.assertEquals( 2.0 * vals[i], vec.get( indexes[i] ) );
		}
	}

	/**
	 * Test method for {@link stream.data.vector.SparseVector#add(double, stream.data.vector.SparseVector)}.
	 */
	@Test
	public void testAdd() {
		System.out.println();
		System.out.println();
		
		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };
		HashMap<Integer,Double> pairs1 = new HashMap<Integer,Double>();
		for(int i=0; i<idx1.length; ++i) {
			pairs1.put(idx1[i], val1[i]);
		}

		int[] idx2= new int[]{ 0, 11, 124 };
		double[] val2 = new double[]{ 0.9d, 2.4d, 2.3d };
		HashMap<Integer,Double> pairs2 = new HashMap<Integer,Double>();
		for(int i=0; i<idx2.length; ++i) {
			pairs2.put(idx2[i], val2[i]);
		}

		//SparseVector v1 = new SparseVector( idx1, val1, -1 );
		//SparseVector v2 = new SparseVector( idx2, val2, -1 );
		SparseVector v1 = new SparseVector( pairs1, -1 );
		SparseVector v2 = new SparseVector( pairs2, -1 );
		
		System.out.println( "v1 = " + v1 );
		System.out.println( "v2 = " + v2 );
		
		SparseVector sumv = v1.add( 1.0d, v2 );
		System.out.println( "v1 + v2 = " + sumv );

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
		HashMap<Integer,Double> pairs1 = new HashMap<Integer,Double>();
		for(int i=0; i<idx1.length; ++i) {
			pairs1.put(idx1[i], val1[i]);
		}

		SparseVector v1 = new SparseVector( pairs1, -1 );
		//SparseVector v1 = new SparseVector( idx1, val1, -1 );
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
		HashMap<Integer,Double> pairs1 = new HashMap<Integer,Double>();
		for(int i=0; i<idx1.length; ++i) {
			pairs1.put(idx1[i], val1[i]);
		}

		SparseVector v1 = new SparseVector( pairs1, -1 );
		//SparseVector v1 = new SparseVector( idx1, val1, -1 );
		Assert.assertEquals( 3, v1.size() );
		System.out.println( "Checking size of vector " + v1 );
		System.out.println( "Size is: " + v1.size() );
	}

	@Test
	public void testInnerProduct2(){

		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 9.1d, 1.4d, 2.1d };
		HashMap<Integer,Double> pairs1 = new HashMap<Integer,Double>();
		for(int i=0; i<idx1.length; ++i) {
			pairs1.put(idx1[i], val1[i]);
		}

		SparseVector v1 = new SparseVector( pairs1, -1 );
		//SparseVector v1 = new SparseVector( idx1, val1, -1 );

		double snorm = v1.snorm();
		double selfProd = v1.innerProduct( v1 );
		
		Assert.assertEquals( snorm, selfProd );
	}

	
	@Test
	public void testInnerProduct(){

		int[] idx1= new int[]{ 0, 10, 124 };
		double[] val1 = new double[]{ 10.0d, 2.0d, 3.0d };
		HashMap<Integer,Double> pairs1 = new HashMap<Integer,Double>();
		for(int i=0; i<idx1.length; ++i) {
			pairs1.put(idx1[i], val1[i]);
		}

		int[] idx2= new int[]{ 0, 11, 124 };
		double[] val2 = new double[]{ 0.9d, 2.4d, 2.3d };
		HashMap<Integer,Double> pairs2 = new HashMap<Integer,Double>();
		for(int i=0; i<idx2.length; ++i) {
			pairs2.put(idx2[i], val2[i]);
		}

		SparseVector v1 = new SparseVector( pairs1, -1 );
		SparseVector v2 = new SparseVector( pairs2, -1 );
		//SparseVector v1 = new SparseVector( idx1, val1, -1 );
		//SparseVector v2 = new SparseVector( idx2, val2, -1 );

		double prod = v1.innerProduct( v2 );
		Assert.assertEquals( 9.0d + 6.9d, prod, 0.0000001 );
	}
}