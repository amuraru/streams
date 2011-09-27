package stream.web.servlet;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.storage.DataStorage;
import stream.data.storage.StorageException;

public class DataUploadServlet extends HttpServlet {

	/** The unique class ID 	 */
	private static final long serialVersionUID = 2851709541087857833L;

	static Logger log = LoggerFactory.getLogger( DataUploadServlet.class );



	protected void doUpload( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {

		DataStorage storage = DataStorage.getDataStorage();
		
		try {
			ObjectInputStream ois = new ObjectInputStream( req.getInputStream() );
			Data item = (Data) ois.readObject();
			while( item != null ){
				
				try {
					storage.store( item );
				} catch (StorageException se){
					se.printStackTrace();
				}
				
				item = (Data) ois.readObject();
			}
			ois.close();
		} catch (Exception e) {
			resp.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
		}
	}



	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
		return;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
		return;
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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