package stream.optimization;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.mapper.Mapper;
import stream.data.vector.InputVector;

public class GaussianFeatureMapping implements ApproximateFeatureMapping, Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = -5455524117127186752L;
	
	static Logger log = LoggerFactory.getLogger( GaussianFeatureMapping.class );
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
	//APXGaussianPhi gpu_phi; // = new APXGaussianPhi(gamma, d);
	Mapper<double[],double[]> gpu_phi;

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

	@SuppressWarnings("unchecked")
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
			try {
				Class<?> clazz = Class.forName( "edu.tdo.kernel.GpuKernel.APXGaussianPhi" );
				Constructor<?> con = clazz.getConstructor( Double.class, Integer.class );
				gpu_phi = (Mapper<double[],double[]>) con.newInstance( gamma, dimension );
			} catch (Exception e) {
				throw new RuntimeException( "Failed to instantiate GpuKernel: " + e.getMessage() );
			}
			//gpu_phi = new APXGaussianPhi(gamma, dimension);
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
					//innerprod += bi * entry.getValue();
					Random randG = new Random(System.currentTimeMillis());
					innerprod += bi * Math.sqrt(2.*gamma)*randG.nextGaussian();
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
				//transformed[i] = Math.sqrt(2.0/dimension) * Math.cos(innerprod + 2.*Math.PI*randomBias[i]);
				//if(i<dimension/2)
				//	transformed[i] = Math.sqrt(2.0/dimension) * Math.cos(innerprod);
				//else
				//	transformed[i] = Math.sqrt(2.0/dimension) * Math.sin(innerprod);
				transformed[i] = Math.sqrt(1.0/dimension) * Math.cos(innerprod);
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
			try {
				transformed = gpu_phi.map(vals);
			} catch (Exception e) {
				log.error( "Failed to run transformation: {}", e.getMessage() );
			}
		}

		//return new SparseVector(this.index, v, x.getLabel());
		//return new InputVector(pairs, x.getLabel());
		return new InputVector(transformed, false, x.getLabel());
	}
}
