/**
 * 
 */
package stream.io;

import org.jwall.web.audit.AuditEvent;

import stream.data.DataImpl;

/**
 * <p>
 * This class is a thin adapter to inject audit-events into Data objects, which
 * can be processed by data-streams.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class LogData extends DataImpl {
	
	/** The unique class ID */
	private static final long serialVersionUID = 932034414171905263L;

	public LogData(){
	}
	
	public LogData( AuditEvent evt ){
		for( String var : evt.getVariables() )
			put( var.toUpperCase(), evt.get( var ) );
	}
}