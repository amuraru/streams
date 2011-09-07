package stream.optimization;

import stream.data.vector.Vector;

public interface Kernel {

	
	public final static Kernel LINEAR_KERNEL = new Kernel(){
		@Override
		public double product(Vector x1, Vector x2) {
			return x1.innerProduct( x2 );
		}
	};
	
	
	
	public double product( Vector x1, Vector x2 );
}
