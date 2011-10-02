/**
 * 
 */
package stream.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.web.layout.Markdown;
import stream.web.services.NoteService;
import stream.web.services.NoteServiceImpl;

/**
 * @author chris
 *
 */
public class SciNotesContext implements ServletContextListener {
	
	static Logger log = LoggerFactory.getLogger( SciNotesContext.class );
	static File imageDirectory = new File( "/tmp" );
	static String imageBaseUrl = "/images/";
	
	public static File getImageDirectory(){
		return imageDirectory;
	}
	
	public static String getImageBaseUrl(){
		return imageBaseUrl;
	}
	
	
	public static String getNote( String key ){
		try {
			NoteService noteService = new NoteServiceImpl();
			String txt = noteService.getNote( key, new Date() );
			if( txt == null )
				return staticNote( key );
			return Markdown.compile( txt );
		} catch (Exception e) {
			log.error( "Failed to load note: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
			return staticNote( key );
		}
	}

	public static String staticNote( String key ){

		try {
			InputStream in = SciNotesContext.class.getResourceAsStream( "/" + key );
			BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
			StringBuffer s = new StringBuffer();
			String line = reader.readLine();
			while( line != null ){
				s.append( line + "\n" );
				line = reader.readLine();
			}
			reader.close();
			return Markdown.compile( s.toString() );
			
		} catch (Exception e) {
			log.error( "Failed to markdown static note '{}': {}", key, e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();
		}

		return "";
	}

	
	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		imageDirectory = new File( arg0.getServletContext().getRealPath( "/images" ) );
		log.info( "Initializing image-directory to {}", imageDirectory );
		
		imageBaseUrl = "/images";
		log.info( "Image base URL is: {}", imageBaseUrl );
	}
	

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}
}