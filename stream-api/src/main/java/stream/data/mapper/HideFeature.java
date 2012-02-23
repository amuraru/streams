package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.DataUtils;
import stream.util.Parameter;

public class HideFeature implements DataProcessor {

	String key;
	
	public String getKey() {
		return key;
	}

	@Parameter( name = "key" )
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		DataUtils.hide( key, data );
		return data;
	}
}