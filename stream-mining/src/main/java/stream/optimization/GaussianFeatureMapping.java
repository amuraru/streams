package stream.optimization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import stream.data.vector.SparseVector;

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
	
	public GaussianFeatureMapping( double gamma, int dimension) {
		this.gamma = gamma;
		this.dimension = dimension;
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
		randGauss = new Random();
		randUnif = new Random();
		randomBasis = new ArrayList<HashMap<Integer,Double>>();
		randomBias = new double[dimension];
		index = new int[dimension];
		for(int i=0; i<dimension; ++i) {
			index[i] = i;
			randomBasis.add( new HashMap<Integer,Double>() );
			randomBias[i] = (2.*Math.PI)*randUnif.nextDouble(); 
		}
	}
	
//	protected double[] newGaussianVector() {
//		double[] v = new double[input_dimension];
//		for(int i=0; i<input_dimension; ++i) {
//			v[i] = (2.*gamma)*randGauss.nextGaussian();
//		}
//		return v;
//	}

	@Override
	public SparseVector transform(SparseVector x) {
		//double[] v = new double[dimension];
		HashMap<Integer,Double> pairs = new HashMap<Integer,Double>();
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
			//v[i] = Math.sqrt(2.0/dimension) * Math.cos(innerprod + randomBias[i]);
			pairs.put(i, Math.sqrt(2.0/dimension) * Math.cos(innerprod + randomBias[i]));
			/*
			if(i<dimension/2.)
				pairs.put(i, Math.sqrt(2.0/dimension) * Math.cos(innerprod));
			else
				pairs.put(i, Math.sqrt(2.0/dimension) * Math.sin(innerprod));
			*/
		}
		
		//return new SparseVector(this.index, v, x.getLabel());
		return new SparseVector(pairs, x.getLabel());
	}

}
