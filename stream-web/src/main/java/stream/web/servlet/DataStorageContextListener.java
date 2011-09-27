package stream.web.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.storage.DataStorage;

public class DataStorageContextListener implements ServletContextListener {

	static Logger log = LoggerFactory.getLogger( DataStorageContextListener.class );
	
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		log.info( "Initializing DataStorage..." );
		DataStorage.getDataStorage();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}
}