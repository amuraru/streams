/**
 * 
 */
package stream.data.storage;

import java.util.ArrayList;
import java.util.List;

import stream.data.storage.model.NoteItem;
import stream.data.storage.model.StatisticsItem;
import stream.data.storage.model.User;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author chris
 *
 */
@XStreamAlias( "SciNotes")
public class SciNotes {

	@XStreamAlias( "Users" )
	List<User> users = new ArrayList<User>();
	
	@XStreamAlias( "Notes")
	List<Note> notes = new ArrayList<Note>();

	@XStreamAlias( "Statistics")
	List<StatisticsItem> statistics = new ArrayList<StatisticsItem>();
	
	
	public SciNotes(){
	}
	
	public void add( User user ){
		users.add( user );
	}
	
	public void add( NoteItem n ){
		notes.add( new Note(n) );
	}
	
	public void add( StatisticsItem s ){
		statistics.add( s );
	}
}
