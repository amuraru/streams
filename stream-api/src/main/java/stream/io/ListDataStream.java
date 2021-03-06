package stream.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;

public class ListDataStream implements DataStream {

	final List<DataProcessor> processors = new ArrayList<DataProcessor>();
	List<Data> data;
	int pos = 0;
	
	public ListDataStream( Collection<? extends Data> items ){
		data = new ArrayList<Data>( items );
		pos = 0;
	}
	
	
	@Override
	public Map<String, Class<?>> getAttributes() {
		return new HashMap<String,Class<?>>();
	}

	@Override
	public Data readNext() throws Exception {
		return readNext( new DataImpl() );
	}

	@Override
	public Data readNext(Data datum) throws Exception {
		if( pos < data.size() ){
			datum.putAll( data.get( pos++ ) );
			return datum;
		}
		
		return null;
	}


	@Override
	public void addPreprocessor(DataProcessor proc) {
		processors.add( proc );
	}


	@Override
	public void addPreprocessor(int idx, DataProcessor proc) {
		processors.add( idx, proc );
	}


	@Override
	public List<DataProcessor> getPreprocessors() {
		return processors;
	}
	
	
	public void close(){
		data.clear();
	}
}