package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;

public class RemoveAttribute implements DataProcessor {

	String key;
	
	public RemoveAttribute( String key ){
		this.key = key;
	}
	
	
	@Override
	public Data process(Data data) {
		data.remove( key );
		return data;
	}
}