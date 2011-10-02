package stream.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.storage.model.LogMessageItem;
import stream.web.services.LogService;
import stream.web.services.LogServiceImpl;

public class LogServlet extends HttpServlet {

	/** The unique class ID 	 */
	private static final long serialVersionUID = 2851709541087857833L;

	static Logger log = LoggerFactory.getLogger( LogServlet.class );



	protected void doUpload( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
		log.debug( "Processing upload of log-messages..." );
		try {
			LogService logService = new LogServiceImpl();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader( req.getInputStream() ) );
			String line = reader.readLine();
			while( line != null ){
				log.debug( "line: {}", line );
				//
				// token should be  time, level, key, msg
				//
				String[] token = line.split( ";", 4 );
				String level = token[1];
				String key = token[2];
				String msg = token[3];
				
				logService.log( new Integer( level ) , key, msg );
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (Exception e) {
			resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}



	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.error( "This servlet does not support {}", req.getMethod() );
		resp.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
		return;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		if( req.getParameter( "name" ) != null ){
			resp.setContentType( "text/plain" );
			LogService logService = new LogServiceImpl();
			List<LogMessageItem> msgs = logService.getLogs( req.getParameter("name"), 0, 1000 );
			
			for( LogMessageItem lm : msgs ){
				resp.getWriter().print( lm.getDate() );
				resp.getWriter().print( "  " );
				resp.getWriter().println( lm.getMessage() );
			}
			return;
		}
		
		log.error( "This servlet does not support {}", req.getMethod() );
		resp.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
		return;
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.error( "This servlet does not support {}", req.getMethod() );
		resp.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doUpload(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doUpload(req, resp);
	}
}