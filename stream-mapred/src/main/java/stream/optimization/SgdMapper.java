package stream.optimization;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataUtils;
import stream.data.vector.Vector;
import stream.io.DataStreamWriter;
import stream.io.SparseDataStreamWriter;
import stream.mapred.StatefulStreamMapper;

public class SgdMapper 
	extends StatefulStreamMapper
{
	public static Integer id = 0;
	static Logger log = LoggerFactory.getLogger( SgdMapper.class );

	Double lambda = 3.08e-8;
	Integer myId = -1;
	String weightKey = "w_t";
	StochasticGradientDescent sgd;
	final List<Data> block = new ArrayList<Data>();
	DataStreamWriter writer;
	
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
		
		if( System.getProperty( "lambda" ) != null ){
			lambda = new Double( System.getProperty( "lambda" ) );
		}
		
		loss.setLambda( lambda );
		sgd = new StochasticGradientDescent( loss );
		//sgd.useGaussianKernel(0.001, 2048, false);
		sgd.init();
	}

	
	public void setLambda( Double d ){
		this.lambda = d;
		SvmHingeLoss loss = new SvmHingeLoss();
		loss.setLambda( lambda );
		sgd = new StochasticGradientDescent( loss );
		sgd.init();
	}

	public Double getLambda(){
		return lambda;
	}

	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		// check for the end of the current block
		//
		if( data != null ){
			sgd.learn( data );
		}
		
		return data;
	}


	/**
	 * @see stream.mapred.Mapper#startBlock()
	 */
	@Override
	public void init() {
		//
		// this is for stateful persistent weight vectors only
		//
		Vector w_t = (Vector) recall( weightKey );
		if( w_t != null ){
			log.debug( "Found previous weights vector with key '{}'!", weightKey );
			sgd.setWeightVector( w_t );
		}
	}


	/**
	 * @see stream.mapred.Mapper#endBlock()
	 */
	@Override
	public void finish() {
		
		writer = new SparseDataStreamWriter( this.getWriter() );

		log.debug( "Writing out weight-vector" );
		Data data = new DataImpl();
		data.put( "@w_id", myId );
		Vector out = sgd.getWeightVector();
		DataUtils.put( data, out );
		data.put( "b", sgd.getIntercept() );
		writer.dataArrived(data);
		writer.close();
		//getWriter().println( "w_" + myId + "\t" + sgd.getWeightVector().toString() );
		log.debug( "Remembering new weights vector as '{}'", weightKey );
		remember( weightKey, sgd.getWeightVector() );

	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		InputStream in = System.in;
		OutputStream out = System.out;
		
		String source = "http://kirmes.cs.uni-dortmund.de/data/ccat.tr";
		//source = "http://kirmes.cs.uni-dortmund.de/data/mnist-block.svm_light";
		//source = "http://kirmes.cs.uni-dortmund.de/data/test-data.svm_light";
		source = "file:///Users/chris/mnist-test.tr";
		in = (new URL( source ).openStream() );
		SgdMapper mapper = new SgdMapper();
		mapper.run( in, out );
	}
}