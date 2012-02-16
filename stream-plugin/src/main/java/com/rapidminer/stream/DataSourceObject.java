/**
 * 
 */
package com.rapidminer.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;

import com.rapidminer.operator.AbstractIOObject;
import com.rapidminer.operator.Annotations;

/**
 * @author chris
 *
 */
public class DataSourceObject extends AbstractIOObject {

	/** The unique class ID */
	private static final long serialVersionUID = 2191156531359947979L;

	static Logger log = LoggerFactory.getLogger( DataSourceObject.class );
	
	Annotations annotations = new Annotations();
	transient DataStream stream;
	
	
	public DataSourceObject( DataStream stream ){
		this.stream = stream;
	}
	
	
	/**
	 * @see com.rapidminer.operator.IOObject#getAnnotations()
	 */
	@Override
	public Annotations getAnnotations() {
		return annotations;
	}

	
	public Data readNext(){
		try {
			return stream.readNext();
		} catch (Exception e) {
			log.error( "Failed to read from data-stream: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
			return null;
		}
	}
}