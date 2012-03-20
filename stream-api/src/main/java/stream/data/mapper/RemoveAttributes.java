package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.Description;
import stream.util.ParameterUtils;


/**
 * This class implements a data-processor that removes a bunch of
 * keys from each processed data item. Keys can be specified as a
 * list:
 * <pre>
 *    &lt;RemoveAttributes keys="a,b,c" /&gt;
 * </pre>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Description( group="Data Stream.Processing.Transformations.Attributes" )
public class RemoveAttributes implements DataProcessor {

	String[] keys = new String[0];
	
	public RemoveAttributes(){
	}
	
	public RemoveAttributes( String keyString ){
		setKeys( keyString );
	}
	
	
	public void setKeys( String keyString ){
		keys = ParameterUtils.split( keyString );
	}
	
	public String getKeys(){
		return ParameterUtils.join( keys );
	}
	
	
	@Override
	public Data process(Data data) {
		for( String key : keys )
			data.remove( key );
		return data;
	}
}