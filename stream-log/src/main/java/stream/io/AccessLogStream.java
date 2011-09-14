/**
 * 
 */
package stream.io;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jwall.web.audit.AuditEvent;
import org.jwall.web.audit.io.AccessLogAuditReader;

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
public class AccessLogStream 
	implements DataStream {

	AccessLogAuditReader reader;
	AuditEvent pending = null;
	Map<String,Class<?>> attributes = null;
	
	
	/**
	 * @param url
	 * @throws Exception
	 */
	public AccessLogStream(URL url) throws Exception {
		reader = new AccessLogAuditReader( url.openStream() );
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
		if( evt == null )
		    return null;
		
		return new LogData( evt );
	}
	
	
	public Data readNext( Data data ) throws Exception {
		return readNext();
	}
}