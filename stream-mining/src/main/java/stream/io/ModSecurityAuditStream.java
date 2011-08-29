/**
 * 
 */
package stream.io;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jwall.web.audit.AuditEvent;
import org.jwall.web.audit.io.AuditEventReader;
import org.jwall.web.audit.io.ModSecurity2AuditReader;

import stream.data.Data;

/**
 * <p>
 * This class implements a simple access-log data stream. It provides access to log-data
 * stored in the default Apache access log format.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class ModSecurityAuditStream 
	implements DataStream {

	AuditEventReader reader;
	AuditEvent pending = null;
	Map<String,Class<?>> attributes = null;
	
	
	/**
	 * @param url
	 * @throws Exception
	 */
	public ModSecurityAuditStream(URL url) throws Exception {
		reader = new ModSecurity2AuditReader( url.openStream() );
		pending = reader.readNext();
		
	}

	
	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		if( attributes != null )
			return attributes;
		
		try {
			attributes = new LinkedHashMap<String,Class<?>>();
			pending = reader.readNext();
			for( String var : pending.getVariables() )
				attributes.put( var.toUpperCase(), String.class );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return attributes;
	}

	
	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		
		if( pending != null ){
			Data data = new LogData( pending );
			pending = null;
			return data;
		}

		AuditEvent evt = reader.readNext();
		return new LogData( evt );
	}
	
	
	public Data readNext( Data data ) throws Exception {
		AuditEvent evt = reader.readNext();
		if( data == null ){
			return new LogData( evt );
		} else {
			data.clear();
			for( String var : evt.getVariables() )
				data.put( var.toUpperCase(), evt.get( var ) );
			return data;
		}
	}
}