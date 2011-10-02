/**
 * 
 */
package stream.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.storage.DataStorage;
import stream.data.storage.SciNotes;
import stream.data.storage.model.NoteItem;
import stream.data.storage.model.StatisticsItem;
import stream.data.storage.model.User;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author chris
 *
 */
public class ExportServlet extends HttpServlet {

	/** The unique class ID */
	private static final long serialVersionUID = -3758529128556559192L;

	static Logger log = LoggerFactory.getLogger( ExportServlet.class );
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		File file = new File( "/tmp/export.xml" );
		log.info( "Exporting to {}", file );
		FileOutputStream out = new FileOutputStream( file );
		
		XStream xs = new XStream();
		xs.processAnnotations( SciNotes.class );
		xs.processAnnotations( StatisticsItem.class );
		xs.processAnnotations( NoteItem.class );

		SciNotes notes = new SciNotes();
		OutputStream p = out;
		Session s = null;
		
		try {
			s = DataStorage.getDataStorage().openSession();
			List<?> us = s.createQuery( "from User u" ).list();
			for( Object o : us ){
				notes.add( (User) o );
			}
		} catch (Exception e) {
		} finally {
			s.close();
		}
		
		
		try {
			s = DataStorage.getDataStorage().openSession();
			List<?> list = s.createQuery( "from NoteItem n" ).list();
			for( Object o : list ){
				NoteItem note = (NoteItem) o;
				notes.add( note );
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( s != null )
				s.close();
		}
		
		try {
			s = DataStorage.getDataStorage().openSession();
			List<?> list = s.createQuery( "from StatisticsItem n" ).list();
			for( Object o : list ){
				StatisticsItem note = (StatisticsItem) o;
				notes.add( note );
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( s != null )
				s.close();
		}
		xs.toXML( notes, p );
		p.flush();
	}
	
	
	@XStreamAlias( "Notes")
	public class Notes {
		@XStreamImplicit
		List<NoteItem> notes = new ArrayList<NoteItem>();
		
		public Notes(){
		}
		
		public Notes( Collection<NoteItem> ni ){
			notes.addAll( ni );
		}
		
		public void add( NoteItem n ){
			notes.add( n );
		}
	}
	
	@XStreamAlias( "Statistics" )
	public class Statistics {
		@XStreamImplicit
		List<StatisticsItem> stats = new ArrayList<StatisticsItem>();
		
		public Statistics(){
		}
		
		public void add( StatisticsItem stat ){
			stats.add( stat );
		}
	}
}