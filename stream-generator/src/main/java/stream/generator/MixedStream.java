/**
 * 
 */
package stream.generator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stream.data.Data;
import stream.data.DataImpl;
import stream.io.DataStream;

/**
 * @author chris
 *
 */
public class MixedStream extends GeneratorDataStream {

	Map<String,Class<?>> types = new LinkedHashMap<String,Class<?>>();
	Double totalWeight = 0.0d;
	List<Double> weights = new ArrayList<Double>();
	List<DataStream> streams = new ArrayList<DataStream>();
	
	Random rnd = new Random();
	
	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return types;
	}

	
	
	public void add( Double weight, DataStream stream ){
		streams.add( stream );
		weights.add( totalWeight + weight );
		types.putAll( stream.getAttributes() );
		totalWeight += weight;
	}
	
	
	protected int choose(){
		
		double d = rnd.nextDouble();
		Double t = d * totalWeight;
		
		for( int i = 0; i < weights.size(); i++ ){
			if( i + 1 < weights.size() && weights.get(i + 1) > t )
				return i;
		}
		
		return weights.size() - 1;
	}
	
	
	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext( new DataImpl() );
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {
		int i = this.choose();
		return streams.get(i).readNext( datum );
	}
	
	
	
	public static void main( String[] args ) throws Exception {
		MixedStream ms = new MixedStream();
		ms.readNext();
	}
}