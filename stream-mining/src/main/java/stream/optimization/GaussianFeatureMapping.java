package stream.optimization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import stream.data.vector.InputVector;

import edu.tdo.kernel.GpuKernel.APXGaussianPhi;

public class GaussianFeatureMapping implements ApproximateFeatureMapping {

	int dimension;				// Approximation dimension
	int size=0;					// It grows to input dimension eventually.
	HashSet<Integer> indexes;
	ArrayList<HashMap<Integer,Double>> randomBasis;
	double[] randomBias;
	double gamma;
	Random randGauss;
	Random randUnif;
	int[] index;
	
	double[] transformed;		// An output vector, to avoid memory allocations (NOT thread-safe).
	
	// for GPU
	boolean use_gpu;
	APXGaussianPhi gpu_phi; // = new APXGaussianPhi(gamma, d);
	
	public GaussianFeatureMapping( double gamma, int dimension, boolean use_gpu ) {
		this.gamma = gamma;
		this.dimension = dimension;
		this.use_gpu = use_gpu;
		init();
	}
	
	@Override
	public void setDimension( int dimension ) {
		this.dimension = dimension;
	}

	@Override
	public int getDimension() {
		return dimension;
	}

	@Override
	public void init() {
		if(!use_gpu) {
			randGauss = new Random();
			randUnif = new Random();
			randomBasis = new ArrayList<HashMap<Integer,Double>>();
			randomBias = new double[dimension];
			index = new int[dimension];
			transformed = new double[dimension];
			for(int i=0; i<dimension; ++i) {
				index[i] = i;
				randomBasis.add( new HashMap<Integer,Double>() );
				randomBias[i] = (2.*Math.PI)*randUnif.nextDouble(); 
			}
		} else { // GPU
			gpu_phi = new APXGaussianPhi(gamma, dimension);
		}
	}
	
//	protected double[] newGaussianVector() {
//		double[] v = new double[input_dimension];
//		for(int i=0; i<input_dimension; ++i) {
//			v[i] = (2.*gamma)*randGauss.nextGaussian();
//		}
//		return v;
//	}

	/**
	 * Trasforms a sparse input vector to a dense vector
	 */
	@Override
	public InputVector transform(InputVector x) {
		
		if(!x.isSparse())
			return null;
		
		if(!use_gpu) {

			//HashMap<Integer,Double> pairs = new HashMap<Integer,Double>();
			//int xsize = x.size();
			//int[] xindex = x.getIndexes();
			//double[] xvalues = x.getValues();
			double innerprod = 0.0d;
			
			for(int i=0; i<dimension; ++i) {
				HashMap<Integer,Double> basis = randomBasis.get(i);
				innerprod = 0.0d;
				//for(int j=0; j<xsize; ++j) {
				for(Map.Entry<Integer,Double> entry : x.getPairs().entrySet()) {
					int idx = entry.getKey();
					Double bi = basis.get(idx);
					if(bi==null) {
						bi = new Double(Math.sqrt(2.*gamma)*randGauss.nextGaussian());
						basis.put(idx, bi);
					}
					innerprod += bi * entry.getValue();
					/*
					int idx = xindex[j];
					Double bi = basis.get(idx);
					if(bi==null) {
						bi = new Double((2.*gamma)*randGauss.nextGaussian());
						basis.put(idx, bi);
					}
					innerprod += bi.doubleValue() * xvalues[j];
					*/ 
				}
				transformed[i] = Math.sqrt(2.0/dimension) * Math.cos(innerprod + 2.*Math.PI*randomBias[i]);
				//pairs.put(i, Math.sqrt(2.0/dimension) * Math.cos(innerprod + randomBias[i]));
				/*
				if(i<dimension/2.)
					pairs.put(i, Math.sqrt(2.0/dimension) * Math.cos(innerprod));
				else
					pairs.put(i, Math.sqrt(2.0/dimension) * Math.sin(innerprod));
				*/
			}
		} else {
			int x_len = x.getPairs().size();
			double[] vals;
			if(x_len < 32)
				vals = new double[32];
			else
				vals = new double[x_len];
			int i=0;
			for(double d : x.getPairs().values()) {
				vals[i++] = d;
			}
			/*
			for(; i<64; ++i)
				vals[i] = 0.0;
			*/
			transformed = gpu_phi.transform(vals);
		}
		
		//return new SparseVector(this.index, v, x.getLabel());
		//return new InputVector(pairs, x.getLabel());
		return new InputVector(transformed, false, x.getLabel());
	}

}
