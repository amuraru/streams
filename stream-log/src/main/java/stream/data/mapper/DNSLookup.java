/**
 * 
 */
package stream.data.mapper;

import java.io.Serializable;

import org.jwall.web.audit.processor.DNSLookupProcessor;

import stream.data.Data;
import stream.util.Description;
import stream.util.Parameter;

/**
 * @author chris
 *
 */
@Description( name="DNS Lookup", group="Data Stream.Processing.Annotations" )
public class DNSLookup
	extends DNSLookupProcessor {

	/**
	 * @see org.jwall.web.audit.processor.DNSLookupProcessor#setKey(java.lang.String)
	 */
	@Override
	@Parameter( required=true, defaultValue="REMOTE_ADDR" )
	public void setKey(String key) {
		super.setKey(key);
	}
	

	/**
	 * @see org.jwall.web.audit.processor.DNSLookupProcessor#setTarget(java.lang.String)
	 */
	@Override
	@Parameter( required=true, defaultValue="REMOTE_HOSTNAME" )
	public void setTarget(String target) {
		super.setTarget(target);
	}

	
	

	/**
	 * @see org.jwall.web.audit.processor.DNSLookupProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		if( data == null )
			return data;
		
		Serializable value = data.get( getKey() );
		if( value != null ){
			String hostname = resolve( value.toString() );
			if( hostname != null )
				data.put( getTarget(), hostname + "" );
		}
		
		return data;
	}
	
	
	protected String resolve( String name ){
		return lookup( name );
	}
}