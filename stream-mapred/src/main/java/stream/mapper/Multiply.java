/**
 * 
 */
package stream.mapper;

import stream.data.Data;
import stream.data.DataImpl;
import stream.mapred.StreamMapper;

/**
 * @author chris
 *
 */
public class Multiply extends StreamMapper {
	
	Double factor = 1.0d;
	
	
	/**
	 * @return the factory
	 */
	public Double getFactor() {
		return factor;
	}


	/**
	 * @param factory the factory to set
	 */
	public void setFactor(Double factor) {
		this.factor = factor;
	}

	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		Data result = new DataImpl();
		
		for( String key : data.keySet() ){
			try {
				Double d = new Double( data.get( key ).toString() );
				result.put( key, factor * d );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			write( result );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
}