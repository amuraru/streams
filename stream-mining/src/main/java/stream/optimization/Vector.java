package stream.optimization;

public interface Vector {

	public void set( int i, double d );
	
	public double get( int i );
	
	public Vector scale( double d );
	
	public Vector add( Vector vec );
	
	public double norm();
	
	public double snorm();
	
	public double innerProduct( Vector vec );
}