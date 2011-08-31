package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.DataUtils;
import stream.util.Parameter;

public class HideFeature implements DataProcessor {

	@Parameter( name = "key" )
	String key;

	String exp;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	
	public String getRegex(){
		return exp;
	}
	
	public void setRegex( String str ){
		exp = str;
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
