/**
 * 
 */
package stream.web.services;

import java.util.List;

import stream.data.storage.model.LogMessageItem;

/**
 * @author chris
 *
 */
public interface LogService {

	public void log( Integer level, String tag, String message );
	
	public List<LogMessageItem> getLogs( String tag, int offseet, int limit  );
}