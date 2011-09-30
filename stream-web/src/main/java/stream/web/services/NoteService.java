/**
 * 
 */
package stream.web.services;

import java.util.Date;
import java.util.List;
import java.util.Set;

import stream.web.NoteRef;

/**
 * @author chris
 *
 */
public interface NoteService {

	public String getNote( String key );
	
	public String getNote( String key, Date date );
	
	public void saveNote( String key, String txt );
	
	public List<Date> getChanges( String key );
	
	public Set<NoteRef> search( String query );
}
