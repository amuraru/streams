/**
 * 
 */
package stream.data.vector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;

/**
 * @author chris / Modified by Sangkyun
 *
 */
public class SparseVector implements Serializable, Vector
{
	/** The unique class ID */
	private static final long serialVersionUID = 8773169606200673610L;

	HashMap<Integer,Double> pairs;
	//int size = 0;
	//int[] indexes = new int[0];
	//double[] values = new double[0];
	double y;

	
	public SparseVector(){
		//indexes = new int[0];
		//values = new double[0];
		pairs = new HashMap<Integer,Double>();
		y = 0.0d;
	}

	public SparseVector( SparseVector vec ){
		this.pairs = new HashMap<Integer,Double>(vec.pairs);
		y = vec.y; 
		//this( vec.indexes, vec.values, vec.y, true );
	}

	//public SparseVector( int[] indexes, double[] values, double y) {
	public SparseVector( HashMap<Integer,Double> pairs, double y) {
		//this( indexes, values, y, true );
		this( pairs, y, true );
	}

	//public SparseVector( int[] indexes, double[] values, double y, boolean copy ){
	public SparseVector( HashMap<Integer,Double> pairs, double y, boolean copy ){
		if( !copy ){
			//this.indexes = indexes;
			//this.values = values;
			this.pairs = pairs;
		} else {
			this.pairs = new HashMap<Integer,Double>(pairs);
			/*
			this.indexes = new int[ indexes.length ];
			this.values = new double[ indexes.length ];

			for( int i = 0; i < indexes.length; i++ ){
				this.indexes[i] = indexes[i];
				this.values[i] = values[i];
			}
			*/
		}

		this.y = y;

		/*
		int j = 0;

		while( j < indexes.length && indexes[j] >= 0 && !Double.isNaN( values[j]) )
			j++;
		
		size = j;
		*/
	}
	
	public double getLabel(){
		return y;
	}
	
	/*
	public int[] getIndexes() {
		return indexes;
	}
	
	public double[] getValues() {
		return values;
	}
	*/
	public HashMap<Integer,Double> getPairs() {
		return pairs;
	}

	public Vector scale( double d ){
		
		for( Map.Entry<Integer, Double> entry : pairs.entrySet() ) {
			entry.setValue( d * entry.getValue() );
		}
		/*
		for( int i = 0; i < size; i++ )
			values[i] = d * values[i];
		*/
		return this;
	}

	public SparseVector add( double factor, SparseVector x ){

		for(Map.Entry<Integer, Double> entry : x.pairs.entrySet()) {
			Integer key = entry.getKey();
			Double val = this.pairs.get(key);
			if(val==null)
				this.pairs.put(key, factor * entry.getValue() );
			else {
				//val += factor * entry.getValue();
				this.pairs.put(key, val + factor * entry.getValue());
			}
		}
		/*
		int[] rind = new int[ size + x.size ];
		double[] rval = new double[ size + x.size ];

		int i = 0;
		int j = 0;
		int k = 0;
		//int eq = 0;

		while( i < size || j < x.size ){

			if( indexes[i] == x.indexes[j] ) {
				rind[k] = indexes[i];
				rval[k++] = values[i++] + factor * x.values[j++];
				//eq++;
			} else {
				
				if( indexes[i] < x.indexes[j] ){
					rind[k] = indexes[i];
					rval[k++] = values[i++];
				} else {
					rind[k] = x.indexes[j];
					rval[k++] = factor * x.values[j++];
				}
			}
		}

		while( i < size ){
			rind[k] = indexes[i];
			rval[k++] = values[i++];
		}


		while( j < x.size ){
			rind[k] = x.indexes[j];
			rval[k++] = x.values[j++];
		}

		
		//System.out.println( "Adding vectors of sizes " + size + " and " + x.size );
		//System.out.println( "Memory size of new Vector is: " + rind.length );
		//System.out.println( "Adding two vectors with " + eq + " overlapping indexes returned sum of size " + k );
		
		SparseVector vec = new SparseVector( rind, rval, y, false );
		vec.size = k;
		return vec;
		*/
		return this;
	}
	
	
	public double innerProduct( SparseVector x ){
		double sum = 0.0d;
		
		for(Map.Entry<Integer, Double> entry : x.pairs.entrySet()) {
			Integer key = entry.getKey();
			Double val = this.pairs.get(key);
			if(val!=null)
				sum += val * entry.getValue();
		}		
		/*
		int i = 0;
		int j = 0;
		double sum = 0.0d;

		while( i < size && j < x.size ){

			if( indexes[i] == x.indexes[j] ) {
				sum += values[i++] * x.values[j++];
			} else {
				
				if( indexes[i] < x.indexes[j] ){
					i++;
				} else {
					j++;
				}
			}
		}
		*/
		
		return sum;
	}
	
	/*
	public double innerProduct( double[] x ){
		int i = 0;
		int j = 0;
		double sum = 0.0d;

		while( i < size ){

			if( indexes[i] == j ) {
				sum += values[i++] * x[j++];
			} else {
				
				if( indexes[i] < j ){
					i++;
				} else {
					j++;
				}
			}
		}
		
		return sum;
	}
	*/

	public double get( int i ){
		/*
		for( int k = 0; k < size; k++ ){
			if( indexes[k] >= 0 && indexes[k] == i )
				return values[k];

			if( indexes[k] < 0 || indexes[k] > i )
				return 0.0d;
		}

		return 0.0d;
		*/
		return pairs.get(i);
	}

	
	public double norm(){
		return Math.sqrt( snorm() );
	}
	

	public double snorm(){
		/*
		double sum = 0.0d;

		for( int i = 0; i < size; i++ )
			sum += values[i] * values[i];

		return sum;
		*/
		return innerProduct(this);
	}


	public int size(){
		//return size;
		return pairs.size();
	}
	
	public int memSize(){
		//return indexes.length;
		return pairs.size();
	}

	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append( y );
		s.append( " | " );
		for( Map.Entry<Integer, Double> entry : pairs.entrySet()){
			if(entry.getValue() > 0.0d) {
				s.append( " " );
				s.append( entry.getKey() );
				s.append( ":" );
				s.append( entry.getValue() );
			}
		}

		return s.toString();

		/*
		StringBuffer s = new StringBuffer();
		s.append( y );
		s.append( " | " );
		for( int i = 0; i < indexes.length && indexes[i] >= 0; i++ ){
			s.append( " " );
			s.append( indexes[i] );
			s.append( ":" );
			s.append( values[i] );
		}

		return s.toString();
		*/
	}

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#set(int, double)
	 */
	@Override
	public void set(int j, double d) {

		this.pairs.put(j, d);
		/*
		int pos = -1;
		
		for( int i = 0; i < indexes.length; i++ ){
			if( indexes[i] == j ){
				values[i] = d;
				return;
			}
			
			if( pos < 0 && indexes[i] > j )
				pos = i;
		}
		
		
		int[] rind = new int[ indexes.length + 1 ];
		double[] rval = new double[ indexes.length + 1 ];
		
		int off = 0;
		
		for( int k = 0; k < indexes.length; k++ ){
			if( k == pos ){
				rind[k] = j;
				rval[k] = d;
				off = 1;
			} else {
				rind[k + off] = indexes[k];
				rval[k + off] = values[k];
			}
		}
			
		this.indexes = rind;
		this.values = rval;
		*/
	}
	

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#add(stream.data.vector.Vector)
	 */
	@Override
	public void add(Vector vec) {
		
	}

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#add(double, stream.data.vector.Vector)
	 */
	@Override
	public void add(double scale, Vector vec) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see stream.data.vector.Vector#innerProduct(stream.data.vector.Vector)
	 */
	@Override
	public double innerProduct(Vector vec) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	/**
	 * @see stream.data.vector.Vector#getNumberOfNonZeros()
	 */
	@Override
	public int getNumberOfNonZeros(){
		//return size;
		return size();
	}
	

	/**
	 * The idealized by size of this vector is the sum of the number of indexes,
	 * the number of values and the label.
	 * 
	 * Indexes are stored as 32bit int, values as 64bit double, the label is stored 
	 * as 64bit double.
	 * 
	 *  @see stream.data.Measurable#getByteSize()
	 */
	@Override
	public double getByteSize() {
		//
		// the idealized by size of this vector is the sum of the number of indexes,
		// the number of values and the label.
		//
		// indexes are stored as 32bit int, values as 64bit double
		// the label is stored as 64bit double
		//
		//return 4.0d * indexes.length + 8.0 * indexes.length + 8.0d;
		return 0.0d;		// TODO
	}

	@Override
	public int getMaxIndex() {
		/*
		if( size == 0 )
			return -1;
		
		return indexes[size];
		*/
		return -1;	//TODO
	}
}