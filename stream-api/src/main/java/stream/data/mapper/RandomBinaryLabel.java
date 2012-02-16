/**
 * 
 */
package stream.data.mapper;

import java.util.Random;

import stream.data.Data;
import stream.data.DataProcessor;

/**
 * @author chris
 *
 */
public class RandomBinaryLabel implements DataProcessor {

	Long seed = System.currentTimeMillis();
	Random random = new Random();
	String labelAttribute = "@label";
	
	
	/**
	 * @return the seed
	 */
	public Long getSeed() {
		return seed;
	}
	

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(Long seed) {
		this.seed = seed;
		if( seed != null )
			random = new Random( seed );
	}


	/**
	 * @return the labelAttribute
	 */
	public String getKey() {
		return labelAttribute;
	}


	/**
	 * @param labelAttribute the labelAttribute to set
	 */
	public void setKey(String labelAttribute) {
		this.labelAttribute = labelAttribute;
	}


	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		Double val = random.nextDouble();
		if( val < 0.5 ){
			data.put( labelAttribute, -1.0d );
		} else
			data.put( labelAttribute, 1.0d );
		
		return data;
	}
}