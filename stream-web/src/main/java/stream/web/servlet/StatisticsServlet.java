/**
 * 
 */
package stream.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.storage.DataStorage;
import stream.io.CsvStream;
import stream.io.DataStreamWriter;

/**
 * @author chris
 *
 */
public class StatisticsServlet extends HttpServlet {

	/** The unique class ID */
	private static final long serialVersionUID = 3573060904996365582L;

	static Logger log = LoggerFactory.getLogger( StatisticsServlet.class );
	
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String key = getKey( req );
		if( key != null ){
			log.info( "Key is: '{}'", key );
			
			DataStorage storage = DataStorage.getDataStorage();
			try {
				List<Data> stats = storage.getStatistics( key );

				DataStreamWriter writer = new DataStreamWriter( resp.getOutputStream() );
				for( Data item : stats ){
					writer.dataArrived( item );
				}
				
				writer.close();
				return;
				
			} catch (Exception e) {
				log.error( "Failed to write statistics output: {}", e.getMessage() );
				if( log.isDebugEnabled() )
					e.printStackTrace();
			}
		}

		log.error( "Invalid URL: {}", req.getRequestURI() );
		resp.sendError( HttpServletResponse.SC_BAD_REQUEST );
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

		log.info( "Appending to statistics with key {}", key );
		
		try {
			DataStorage storage = DataStorage.getDataStorage();
			File file = getStatisticsFile( key );
			log.info( "Writing to file {}", file );
			DataStreamWriter writer = new DataStreamWriter( new FileOutputStream( file, true ) );
			
			CsvStream stream = new CsvStream( req.getInputStream() );
			Data item = stream.readNext();
			int i = 0;
			while( item != null ){
				writer.dataArrived( item );
				try {
					storage.addStatistics( key, item );
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
				item = stream.readNext();
			}
			log.info( "{} items appended", i );
			writer.close();
			
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
		log.info( "base: {}", base );
		log.info( "servlet: {}", servlet );
		
		log.info( "prefix: {}", prefix );
		log.info( "URI: {}", req.getRequestURI() );

		try {
			return req.getRequestURI().substring( prefix.length() + 1 );
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