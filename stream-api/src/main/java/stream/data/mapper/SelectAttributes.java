/**
 * 
 */
package stream.data.mapper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * @author chris
 *
 */
public class SelectAttributes implements DataProcessor {

	String[] keys = new String[0];
	
	Set<String> selected = new HashSet<String>();
	
	/**
	 * @return the keys
	 */
	public String getKeys() {
		if( keys == null || keys.length == 0 )
			return "";
		
		StringBuffer s = new StringBuffer();
		for( int i = 0; i < keys.length; i++ ){
			s.append( keys[i] );
			if( i+1 < keys.length )
				s.append( "," );
		}
		return s.toString();
	}





	/**
	 * @param keys the keys to set
	 */
	public void setKeys(String keys) {
		this.keys = keys.split( "," );
		
		for( String key : this.keys ){
			selected.add( key );
		}
	}





	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		if( keys == null || keys.length == 0 ){
			return data;
		}

		Iterator<String> it = data.keySet().iterator();
		while( it.hasNext() ){
			String key = it.next();
			if( !selected.contains( key ) ){
				it.remove();
			}
		}
		
		return data;
	}
}
