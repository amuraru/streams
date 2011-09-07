package stream.data.vector;

import stream.data.Measurable;

public interface Vector
	extends Measurable
{

	public void set( int i, double d );
	
	public double get( int i );
	
	public Vector scale( double d );
	
	public void add( Vector vec );
	
	public void add( double scale, Vector vec );
	
	public double norm();
	
	public double snorm();
	
	public double innerProduct( Vector vec );
	
	public double getLabel();
	
	public int getMaxIndex();
	
	public int getNumberOfNonZeros();
}
