/**
 * 
 */
package stream.web.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.web.layout.Markdown;
import stream.web.services.NoteService;
import stream.web.services.NoteServiceImpl;

/**
 * @author chris
 *
 */
public class NotesServlet extends HttpServlet {

	/** The unique class ID */
	private static final long serialVersionUID = 3573060904996365582L;

	static Logger log = LoggerFactory.getLogger( NotesServlet.class );
	
	NoteService noteService;
	
	
	public NotesServlet(){
		noteService = new NoteServiceImpl();
	}
	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String key = getKey( req );
		if( key != null ){
			
			Long time = System.currentTimeMillis();
			try {
				time = Long.parseLong( req.getParameter( "time" ) );
			} catch (Exception e){
				time = System.currentTimeMillis();
			}
			
			if( req.getRequestURI().endsWith( ".edit" ) || req.getParameter( "edit" ) != null )
				req.setAttribute( "edit", "true" );
			
			log.debug( "Key is: '{}'", key );
			if( "".equals( key ) ){
				log.debug( "Empty key specified, delegating to note-list" );
				RequestDispatcher disp = req.getRequestDispatcher( "/notes.jsp" );
				disp.forward( req, resp );
				return;
			}
			
			String txt = noteService.getNote( key, new Date( time ) );
			log.trace( "note text is:\n{}", txt );
			String html = "";
			if( txt != null ){
				html = Markdown.compile( txt );
				List<Date> history = noteService.getChanges( key );
				req.setAttribute( "history", history );
			} else {
				
				if( txt == null && !req.getRequestURI().endsWith( ".edit" ) ){
					resp.sendError( HttpServletResponse.SC_NOT_FOUND );
					return;
				}
				
				txt = "";
				html = "";
			}

			req.setAttribute( "key", key );
			req.setAttribute( "MARKDOWN_RAW", txt );
			req.setAttribute( "MARKDOWN_HTML", html );
			RequestDispatcher dispatcher = req.getRequestDispatcher( "/note.jsp" );
			log.debug( "RequestDispatcher for 'note.jsp' is: {}", dispatcher );
			if( dispatcher != null ){
				log.debug( "forwarding request using request-dispatcher" );
				dispatcher.forward( req, resp );
				return;
			}

			/*
			String templateName = "/markdown.template";
			if( req.getParameter( "edit" ) != null || req.getRequestURI().endsWith( ".edit" ) ){
				templateName = "/markdown-edit.template";
			}
			
			URL template = NotesServlet.class.getResource( templateName );
			log.debug( "Template {} is at {}", templateName, template );
			if( template != null ){
				
				BufferedReader reader = new BufferedReader( new InputStreamReader( template.openStream() ) );
				String line = reader.readLine();
				while( line != null ){
					
					if( line.indexOf( "%{KEY}" ) > 0 ){
						line = line.replace( "%{KEY}", key );
					}
					
					if( line.indexOf( "%{MARKDOWN_RAW}" ) >= 0 )
						line = line.replace( "%{MARKDOWN_RAW}", txt );
					
					if( line.indexOf( "%{MARKDOWN}") >= 0 ){
						line = line.replace( "%{MARKDOWN}", html );
					}
					resp.getWriter().println( line );
					line = reader.readLine();
				}
				reader.close();
				
			} else
				resp.getWriter().print( html );
			 */
			return;
		}

		log.error( "Invalid URL: {}", req.getRequestURI() );
		resp.sendError( HttpServletResponse.SC_NOT_FOUND );
	}

	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String key = getKey( req );
		if( key == null ){
			resp.sendError( HttpServletResponse.SC_BAD_GATEWAY );
			return;
		}

		log.debug( "Appending to statistics with key {}", key );
		
		try {
			
			StringBuffer s = new StringBuffer();
			BufferedReader reader = new BufferedReader( new InputStreamReader( req.getInputStream() ) );
			String line = reader.readLine();
			while( line != null ){
				s.append( line + "\n" );
				line = reader.readLine();
			}
			
			String note = null;
			
			String[] keyVals = s.toString().split( "&" );
			for( String kv : keyVals ){
				String[] pair = kv.split( "=" );
				String k = pair[0];
				String v = URLDecoder.decode( pair[1], "UTF-8" );
				if( "text".equals( k ) ){
					note = v;
				}
			}
			
			if( note != null ){
				noteService.saveNote( key, note );
			}
			resp.sendRedirect( req.getContextPath() + "/notes/" + key );
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	
	protected String getKey( HttpServletRequest req ){

		String base = req.getContextPath();
		String servlet = req.getServletPath();
		String prefix = base + servlet;
		log.debug( "base: {}", base );
		log.debug( "servlet: {}", servlet );
		
		log.debug( "prefix: {}", prefix );
		log.debug( "URI: {}", req.getRequestURI() );

		try {
			String key = req.getRequestURI().substring( prefix.length() + 1 );
			if( key.endsWith( ".edit" ) || key.endsWith( ".html" ) ){
				return key.replaceAll( "\\.(html|edit)$", "" );
			} else {
				return key;
			}
			
		} catch (Exception e) {
			log.error( "Failed to determine key from URI '{}': {}", req.getRequestURI(), e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
			return null;
		}
	}
	
	public File getStatisticsFile( String key ){
		return new File( File.separator + "tmp" + File.separator + key + ".stats" );
	}
}