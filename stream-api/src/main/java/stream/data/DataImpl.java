/**
 * 
 */
package stream.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chris
 *
 */
public class DataImpl extends LinkedHashMap<String, Serializable> implements Data {

	/** The unique class ID */
	private static final long serialVersionUID = -7751681008628413236L;

	public DataImpl(){
	}
	
	public DataImpl( Map<String,Serializable> data ){
		super( data );
	}
}
