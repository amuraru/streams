package stream.hadoop;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.vector.Vector;
import stream.optimization.StochasticGradientDescent;
import stream.optimization.SvmHingeLoss;

public class SgdMapper 
	extends StatefulStreamMapper
{
	public static Integer id = 0;
	static Logger log = LoggerFactory.getLogger( SgdMapper.class );

	Integer myId = -1;
	String weightKey = "w_t";
	StochasticGradientDescent sgd;
	
	public SgdMapper(){
		synchronized( id ){
			myId = id++;
		}
		SvmHingeLoss loss = new SvmHingeLoss();   // Sang: I think it should be called as SVMPrimalHingeLoss
		double lambda = 0.0; 
		lambda = 3.08e-8; // adult
		//lambda = 1.72e-7; // mnist
		//lambda = 1.28e-6; // ccat
		//lambda = 8.82e-8; // ijcnn1
		//lambda = 7.17e-7; // covtype
		//lambda = 1.0e-8;  // mnist-1m 
		loss.setLambda( lambda );
		sgd = new StochasticGradientDescent( loss );
		//sgd.useGaussianKernel(0.001, 2048, false);
		sgd.init();
	}


	@Override
	public void process(List<Data> items) {
		
		Vector w_t = (Vector) recall( weightKey );
		if( w_t != null )
			log.debug( "Found previous weights vector with key '{}'!", weightKey );
		
		log.debug( "Training SGD ({})", sgd );
		
		for( Data item : items ){
			sgd.learn( item );
		}

		log.debug( "Writing out weight-vector" );
		getWriter().println( "w_" + myId + "\t" + sgd.getWeightVector().toString() );
		log.debug( "Remembering new weights vector as '{}'", weightKey );
		remember( weightKey, sgd.getWeightVector() );
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		InputStream in = System.in;
		OutputStream out = System.out;

		//String source = "http://kirmes.cs.uni-dortmund.de/data/ccat.tr";
		//source = "http://kirmes.cs.uni-dortmund.de/data/mnist-block.svm_light";
		//source = "http://kirmes.cs.uni-dortmund.de/data/test-data.svm_light";
		//in = (new URL( source ).openStream() );
		SgdMapper mapper = new SgdMapper();
		mapper.run( in, out );
	}
}