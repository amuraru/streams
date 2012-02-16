/**
 * 
 */
package stream.data.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.Parameter;

/**
 * @author chris
 *
 */
public class MapKeys implements Mapper<Data, Data>, DataProcessor {

	static Logger log = LoggerFactory.getLogger( MapKeys.class );
	String oldKey;
	String newKey;
	String map;
    Map<String,String> mapping = new LinkedHashMap<String,String>();

	
	public MapKeys( String oldKey, String newKey ){
		this.oldKey = oldKey;
		this.newKey = newKey;
	}
	
	
	public MapKeys(){
		this.oldKey = "";
		this.newKey = "";
	}
	
	
	/**
	 * @return the oldKey
	 */
	public String getFrom() {
		return oldKey;
	}

	/**
	 * @param oldKey the oldKey to set
	 */
	public void setFrom(String oldKey) {
		this.oldKey = oldKey;
	}

	/**
	 * @return the newKey
	 */
	public String getTo() {
		return newKey;
	}

	/**
	 * @param newKey the newKey to set
	 */
	public void setTo(String newKey) {
		this.newKey = newKey;
	}


	/**
	 * @return the map
	 */
	public String getMap() {
		return map;
	}


	/**
	 * @param map the map to set
	 */
	@Parameter( name = "map", required = false )
	public void setMap(String map) {
		try {
			if( map == null || map.trim().isEmpty() )
				return;
			
			File file = new File( map );
			Properties p = new Properties();
			p.load( new FileInputStream( file ) );

			for( Object key : p.keySet() ){
				mapping.put( key.toString(), p.getProperty( key.toString() ) );
			}
			
			this.map = map;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * @see stream.data.mapper.Mapper#map(java.lang.Object)
	 */
	@Override
	public Data map(Data input) throws Exception {
		if( oldKey != null && newKey != null && input.containsKey( oldKey ) ){
			if( input.containsKey( newKey ) )
				log.warn( "Overwriting existing key '{}'!", newKey );
			
			Serializable o = input.remove( oldKey );
			input.put( newKey, o );
		}
		
		for( String key : mapping.keySet() ){
			if( input.containsKey( key ) ){
				Serializable value = input.remove( key );
				input.put( mapping.get( key ), value );
			}
		}
		
		return input;
	}


	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		try {
			return map( data );
		} catch (Exception e) {
			e.printStackTrace();
			return data;
		}
	}
}