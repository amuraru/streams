/**
 * 
 */
package stream.data.mapper;

import stream.data.Data;


/**
 * @author chris
 *
 */
public class Identity implements Mapper<Data, Data> {

	/**
	 * @see stream.data.mapper.Mapper#map(java.lang.Object)
	 */
	@Override
	public Data map(Data input) throws Exception {
		return input;
	}
}