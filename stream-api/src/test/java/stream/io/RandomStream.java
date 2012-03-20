/**
 * 
 */
package stream.io;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;

/**
 * @author chris
 *
 */
public class RandomStream implements DataStream {

	final List<DataProcessor> processors = new ArrayList<DataProcessor>();
	final Map<String,Class<?>> attributes = new LinkedHashMap<String,Class<?>>();
	
	Random rnd = new Random();
	
	
	public RandomStream(){
		this( System.nanoTime() );
	}
	
	public RandomStream( Long seed ){
		rnd = new Random( seed );
	}
	
	
	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return attributes;
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
	public Data readNext(Data item ) throws Exception {
		item.clear();

		for( String key : attributes.keySet() ){
			item.put( key, rnd.nextDouble() );
		}
		
		for( DataProcessor proc : processors ){
			item = proc.process( item );
		}
		
		return item;
	}
	

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	
	/**
	 * @see stream.io.DataStream#addPreprocessor(stream.data.DataProcessor)
	 */
	@Override
	public void addPreprocessor(DataProcessor proc) {
		processors.add( proc );
	}

	
	/** 
	 * @see stream.io.DataStream#addPreprocessor(int, stream.data.DataProcessor)
	 */
	@Override
	public void addPreprocessor(int idx, DataProcessor proc) {
		processors.add( idx, proc );
	}

	
	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<DataProcessor> getPreprocessors() {
		return processors;
	}
}