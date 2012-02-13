/**
 * 
 */
package stream.generator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import stream.data.Data;
import stream.data.DataImpl;

/**
 * @author chris
 *
 */
public class RandomStream extends GeneratorDataStream {

	Random random;
	Map<String,Class<?>> attributes = new LinkedHashMap<String,Class<?>>();
	Map<String,Object> store = new LinkedHashMap<String,Object>();
	
	public RandomStream(){
		random = new Random();
		attributes.put( "att1", Double.class );
	}


	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return attributes;
	}

	
	/**
	 * @see stream.io.AbstractDataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		Data map = new DataImpl();
		map.put( "att1", next( random ) );
		return map;
	}
	
	
	public Data readNext( Data data ) throws Exception {
		if( data == null )
			return readNext();
		
		data.clear();
		data.put( "att1", next( random ) );
		return data;
	}
	
	
	
	public Double next( Random rnd ){
		return 0.1 * ( 5.0 + rnd.nextGaussian() );
	}
	
	public Object get( String key ){
		return store.get( key );
	}
	
	public Object get( String key, Object init ){
		if( store.get( key ) == null ){
			store.put( key, init );
			return init;
		}
		return store.get( key );
			
	}
	
	public void set( String key, Object val ){
		store.put( key, val );
	}
	
	
	
	public void close(){
	}
}