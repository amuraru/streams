/**
 * 
 */
package stream.data.mapper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.ParameterUtils;

/**
 * @author chris
 *
 */
public class SelectAttributes implements DataProcessor {

	String[] keys = new String[0];
	
	Set<String> selected = new HashSet<String>();
	
	public void setKeys( String keyString ){
		keys = ParameterUtils.split( keyString );
		for( String key : keys )
			selected.add( key );
	}
	
	public String getKeys(){
		return ParameterUtils.join( keys );
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
