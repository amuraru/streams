package stream.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.Test;

import stream.data.vector.InputVector;
import stream.optimization.GaussianFeatureMapping;

import junit.framework.Assert;

public class GuassianFeatureMappingTest {

//	/**
//	 * Test method for {@link stream.optmization.GaussianFeatureMapping}
//	 * @author Sangkyun Lee
//	 */
//	@Test
//	public void testGaussian() {
//		int nExample = 100;
//		int dim = 32;
//		int approxdim = 4096; //16384;
//		double gamma = 0.001;
//		Random rand = new Random();
//		ArrayList<InputVector> dataset = new ArrayList<InputVector>();
//		for(int i=0; i<nExample; ++i) {
//			HashMap<Integer,Double> example = new HashMap<Integer,Double>();;
//			for(int j=0; j<dim; ++j) {
//				example.put(j, rand.nextDouble());
//			}
//			dataset.add(new InputVector(example, false, -1));
//		}
//		
//		GaussianFeatureMapping kernel = new GaussianFeatureMapping(0.001, approxdim, true);
//		ArrayList<InputVector> trans_dataset = new ArrayList<InputVector>();
//		for(InputVector v : dataset) {
//			//System.out.println(v.toString());
//			InputVector transformed = kernel.transform(v);
//			InputVector copy = new InputVector(transformed);
//			trans_dataset.add( copy );
//		}
//		
//		double kernel_ori;
//		double kernel_apx;
//		double fro_norm = 0.0;
//		for(int i=0; i<nExample; ++i) {
//			for (int j=i+1; j<nExample; ++j) {
//				InputVector x1 = dataset.get(i);
//				InputVector x2 = dataset.get(j);
//				InputVector x1tmp = new InputVector(x1);
//				x1tmp.add(-1.0, x2);
//				kernel_ori = Math.exp( -gamma *  x1tmp.snorm() );
//				InputVector t1 = trans_dataset.get(i);
//				InputVector t2 = trans_dataset.get(j);
//				kernel_apx = t1.innerProduct(t2);
//				fro_norm += Math.pow(kernel_ori - kernel_apx, 2 );
//				//System.out.println("|kernel - kernel(approx)| = " + Math.abs(kernel_ori - kernel_apx));
//			}
//		}
//		System.out.println("Frobenious norm = " + Math.sqrt(fro_norm));
//		System.out.println("Frobenious norm (normalized) = " + Math.sqrt(fro_norm) / nExample);
//	
////		double[] easystuff = {0., 1., 2., 3., 4., 5., 6., 7., 8., 9.};
////		InputVector easyvec = new InputVector();
////		for(int i=0; i<easystuff.length; ++i)
////			easyvec.set(i,  easystuff[i]);
////		
////		GaussianFeatureMapping kernel2 = new GaussianFeatureMapping(0.001, 4096, true);
////		InputVector transformed = kernel2.transform(easyvec);
////		System.out.println("transformed vector = " + transformed.toString());
//	}
	
	/**
	 * Test method for {@link stream.optmization.GaussianFeatureMapping}
	 * @author Sangkyun Lee
	 */
	@Test
	public void testGaussianStat() {

		int nRuns = 1000;
		int dim = 10;
		int nExample = 2;
		double gamma = 0.01;
		int maxpower = 14;
		int minpower = 1;
		

		ArrayList<Double> meanvals = new ArrayList<Double>();
		ArrayList<Double> stdvals = new ArrayList<Double>();
		
		for(int p = minpower; p<=maxpower; ++p) {
			int approxdim = (int)Math.pow(2,p);

			// Generate two random input vectors
			Random rand = new Random();
			ArrayList<InputVector> dataset = new ArrayList<InputVector>();
			for(int i=0; i<nExample; ++i) {
				HashMap<Integer,Double> example = new HashMap<Integer,Double>();;
				for(int j=0; j<dim; ++j) {
					example.put(j, rand.nextDouble());
				}
				dataset.add(new InputVector(example, false, -1));
			}
			
			// Create Approx Kernel
			GaussianFeatureMapping kernel = new GaussianFeatureMapping(gamma, approxdim, true);
			
			ArrayList<Double> errorlist = new ArrayList<Double>();
			for(int r = 0; r<nRuns; ++r) {
				
				

				ArrayList<InputVector> trans_dataset = new ArrayList<InputVector>();
				for(InputVector v : dataset) {
					InputVector transformed = kernel.transform(v);
					InputVector copy = new InputVector(transformed);
					trans_dataset.add( copy );
				}
				
				double kernel_ori;
				double kernel_apx;
				double error = 0.0;
				for(int i=0; i<nExample; ++i) {
					for (int j=i+1; j<nExample; ++j) {
						InputVector x1 = dataset.get(i);
						InputVector x2 = dataset.get(j);
						InputVector x1tmp = new InputVector(x1);
						x1tmp.add(-1.0, x2);
						kernel_ori = Math.exp( -gamma *  x1tmp.snorm() );
						InputVector t1 = trans_dataset.get(i);
						InputVector t2 = trans_dataset.get(j);
						kernel_apx = t1.innerProduct(t2);
						error += Math.pow(kernel_ori - kernel_apx, 2 );
						//System.out.println("|kernel - kernel(approx)| = " + Math.abs(kernel_ori - kernel_apx));
					}
				}
				error /= nExample*(nExample+1)/2;
				errorlist.add(error);
			}
			
			double my_mean = 0.0;
			double my_std = 0.0;
			for(double v : errorlist) {
				my_mean += v;
			}
			my_mean /= errorlist.size();
			for(double v : errorlist) {
				my_std += Math.pow( v - my_mean, 2);
			}
			my_std = Math.sqrt(my_std / (errorlist.size() - 1));
			
			meanvals.add(my_mean);
			stdvals.add(my_std);
			System.out.println(approxdim + " " + my_mean + " " + my_std);
		}

	}
	
}
