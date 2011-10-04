/**
 * 
 */
package stream.optimization;

import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.vector.Vector;
import stream.io.SparseDataStream;
import stream.mapred.StreamMapper;

/**
 * @author chris
 *
 */
public class SgdTestMapper extends StreamMapper {

	static Logger log = LoggerFactory.getLogger( SgdTestMapper.class );
	Double testCount = 0.0d;
	Double testErrors = 0.0d;
	String file;
	SgdTester tester;
	StochasticGradientDescent sgd = new StochasticGradientDescent( new SvmHingeLoss() );

	public void setModel( String file ){
		log.info( "Setting 'model' parameter to {}", file );
		this.file = file;
	}

	public String getModel(){
		return this.file;
	}

	/**
	 * @see stream.mapred.Mapper#init()
	 */
	@Override
	public void init() {
		testCount = 0.0d;
		testErrors = 0.0d;

		//
		// load the model file
		//
		try {
			log.info( "Loading model (weights) from {}", file );
			SparseDataStream stream = new SparseDataStream( new FileInputStream( file ) );
			Data weights = stream.readNext();
			Vector weightsVector = Vector.createSparseVector( weights );

			sgd.init();
			sgd.setWeightVector( weightsVector );
			
			if( weights.get( "b" ) != null ){
				log.info( "Using intercept {}", weights.get( "b" ) );
				sgd.setIntercept( (Double) weights.get( "b" ) );
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		Double prediction = sgd.predict( sgd.getWeightVector(), data );
		Double label = new Double( data.get( "@label" ).toString() );
		
		if( prediction * label < 0 ){
			testErrors += 1.0d;
		}
		testCount += 1.0d;
		return data;
	}
	
	
	/**
	 * @see stream.mapred.Mapper#finish()
	 */
	@Override
	public void finish() throws Exception {
		Data data = new DataImpl();
		data.put( "testSetSize", testCount );
		data.put( "testErrors", testErrors );
		write( data );
	}
}