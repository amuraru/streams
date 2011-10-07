/**
 * 
 */
package net.scinotes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import stream.data.Data;
import stream.data.stats.Statistics;

/**
 * <p>
 * This is a simple API interface implementation that provides access to the
 * most important functionality with the SciNotes system.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class SciNotesAPI {
	
	private final static SciNotesService sciNotes = new SciNotesService( "https://api.scinotes.net" );
	
	public final String getNoteRaw( String note ) throws Exception {
		throw new Exception( "Not implemented!" );
	}
	
	public final String getNoteHtml( String note ) throws Exception {
		throw new Exception( "Not implemented!" );
	}

	public final String saveNote( String note, String text ) throws Exception {
		throw new Exception( "Not implemented!" );
	}
	
	
	public final static boolean send( Statistics stats ) throws Exception {
		List<Statistics> statistics = new ArrayList<Statistics>();
		statistics.add( stats );
		return 1 == send( statistics );
	}
	
	public static int send( Collection<Statistics> stats ) throws Exception {
		
		return -1;
	}
	
	public static boolean send( Data data ) throws Exception {
		return false;
	}
}