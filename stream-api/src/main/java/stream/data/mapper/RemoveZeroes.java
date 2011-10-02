/**
 * 
 */
package stream.data.mapper;

import java.util.ArrayList;
import java.util.List;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * @author chris
 *
 */
public class RemoveZeroes implements DataProcessor {

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		List<String> remove = new ArrayList<String>();


		for( String key : data.keySet() ){
			try {
				Double val = new Double( data.get( key ).toString() );
				if( val == 0.0d )
					remove.add( key );
			} catch (Exception e) {
			}
		}

		for( String key : remove ){
			data.remove( key );
		}

		return data;
	}

}
