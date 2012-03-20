/**
 * 
 */
package stream.data.mapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.Description;
import stream.util.Parameter;

/**
 * @author chris
 *
 */
@Description( group="Data Stream.Processing.Transformations.Data", text= "" )
public class MapValueToID implements DataProcessor {

	static Logger log = LoggerFactory.getLogger( MapValueToID.class );
	
	Integer maxId = 0;
	
	String key = "key";
	
	Map<String,Integer> mapping = new HashMap<String,Integer>();
	
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}


	/**
	 * @param key the key to set
	 */
	@Parameter
	public void setKey(String key) {
		this.key = key;
	}


	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		if( key == null ){
			log.error( "No key specified!" );
			return data;
		}
		
		Serializable val = data.get( key );
		if( val == null ){
			log.debug( "No value found in data-item! Skipping that item." );
			return data;
		}

		
		Integer id = mapping.get( val.toString() );
		if( id == null ){
			id = 1 + maxId;
			maxId++;
			log.debug( "Adding new ID {} for value {}", id, val );
			mapping.put( val.toString(), id );
		} else {
			log.debug( "Found existing ID mapping {} => {}", val, id );
		}
		
		mapping.put( key, id );
		data.put( key, id );
		return data;
	}
}