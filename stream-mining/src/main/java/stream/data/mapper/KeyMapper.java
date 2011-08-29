/**
 * 
 */
package stream.data.mapper;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * @author chris
 *
 */
public class KeyMapper implements Mapper<Data, Data>, DataProcessor {

	static Logger log = LoggerFactory.getLogger( KeyMapper.class );
	String oldKey;
	String newKey;
	
	public KeyMapper( String oldKey, String newKey ){
		this.oldKey = oldKey;
		this.newKey = newKey;
	}
	
	
	public KeyMapper(){
		this.oldKey = "";
		this.newKey = "";
	}
	
	
	/**
	 * @return the oldKey
	 */
	public String getOld() {
		return oldKey;
	}

	/**
	 * @param oldKey the oldKey to set
	 */
	public void setOld(String oldKey) {
		this.oldKey = oldKey;
	}

	/**
	 * @return the newKey
	 */
	public String getNew() {
		return newKey;
	}

	/**
	 * @param newKey the newKey to set
	 */
	public void setNew(String newKey) {
		this.newKey = newKey;
	}

	/**
	 * @see stream.data.mapper.Mapper#map(java.lang.Object)
	 */
	@Override
	public Data map(Data input) throws Exception {
		if( input.containsKey( oldKey ) ){
			if( input.containsKey( newKey ) )
				log.warn( "Overwriting existing key '{}'!", newKey );
			
			Serializable o = input.remove( oldKey );
			input.put( newKey, o );
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