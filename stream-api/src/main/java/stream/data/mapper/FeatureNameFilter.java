/**
 * 
 */
package stream.data.mapper;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.Parameter;


/**
 * @author chris
 *
 */
public class FeatureNameFilter 
	implements DataProcessor {
	
	static Logger log = LoggerFactory.getLogger( FeatureNameFilter.class );

	@Parameter( name = "include" )
	String include;
	
	@Parameter( name = "exclude" )
	String exclude;
	
	
	public FeatureNameFilter(){
		include = ".*";
		exclude = null;
	}
	

	/**
	 * @return the include
	 */
	public String getInclude() {
		return include;
	}

	/**
	 * @param include the include to set
	 */
	public void setInclude(String include) {
		this.include = include;
	}

	/**
	 * @return the exclude
	 */
	public String getExclude() {
		return exclude;
	}

	/**
	 * @param exclude the exclude to set
	 */
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}


	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		ArrayList<String> keys = new ArrayList<String>();
		
		for( String key : data.keySet() ){
			if( include == null || key.matches( include ) ){
				if( exclude != null && key.matches(exclude) ){
					log.debug( "Excluding key '{}'" );
					keys.add( key );
				} 
			} else
				keys.add( key );
		}
		for( String key : keys )
			data.remove( key );
		return data;
	}
}