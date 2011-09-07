package stream.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stream.data.Data;
import stream.data.DataImpl;

public class DataStreamLoop extends DataStreamProcessor {

	List<Data> buffer = new ArrayList<Data>();
	
	int ptr = 0;
	boolean inLoop = false;
	Integer bufferSize = 10000;
	Boolean shuffle = true;
	Integer repeat = -1;
	
	public Integer getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(Integer bufferSize) {
		this.bufferSize = bufferSize;
	}

	public Boolean getShuffle() {
		return shuffle;
	}

	public void setShuffle(Boolean shuffle) {
		this.shuffle = shuffle;
	}
	
	public Integer getRepeat() {
		return repeat;
	}

	public void setRepeat(Integer repeat) {
		this.repeat = repeat;
	}

	@Override
	public Data readNext() throws Exception {
		return readNext( new DataImpl() );
	}

	@Override
	public Data readNext(Data data) throws Exception {
		
		if( inLoop ){
			if( ptr >= buffer.size() ){
				
				if( repeat == 1 )
					return null;
				
				if( shuffle ){
					Collections.shuffle( buffer );
				}
				repeat--;
				ptr = 0;
			}
			
			return buffer.get( ptr++ );
		}
		
		Data item = source.readNext( data );
		if( item == null ){
			inLoop = true;
			return readNext( item );
		}
		buffer.add( item );
		return item;
	}
}