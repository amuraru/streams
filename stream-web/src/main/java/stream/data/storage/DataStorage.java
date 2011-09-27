package stream.data.storage;

import java.net.URL;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

import stream.data.Data;

public class DataStorage {

	static DataStorage storage = null;
	SessionFactory sessionFactory;
	
	private DataStorage(){
		try {
			Properties p = new Properties();
			p.setProperty( "hibernate.connection.driver_class", "com.mysql.jdbc.Driver" );
			p.setProperty( "hibernate.connection.url", "jdbc:mysql://localhost:3306/DataStorage?autoReconnect=true" );
			p.setProperty( "hibernate.connection.username", "datastorage" );
			p.setProperty( "hibernate.connection.password", "datastorage" );
			p.setProperty( "hibernate.hbm2ddl.auto", "update" );
			p.setProperty( "hibernate.show_sql", "true" );
			
			try {
				URL url = DataStorage.class.getResource( "/hibernate.properties" );
				if( url != null ){
					Properties ext = new Properties();
					ext.load( url.openStream() );
					for( Object k : ext.keySet() ){
						p.setProperty( k.toString(), ext.getProperty( k.toString() ) );
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			AnnotationConfiguration cfg = new AnnotationConfiguration()
			.addAnnotatedClass( stream.data.storage.model.DataItem.class )
			.setProperties( p )
			.setNamingStrategy( new DefaultComponentSafeNamingStrategy() );

			sessionFactory = cfg.configure().buildSessionFactory();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataStorage getDataStorage(){
		if( storage == null )
			storage = new DataStorage();
		return storage;
	}
	
	public void store( Data item ) throws StorageException {
		throw new StorageException( "Storage not available!" );
	}
}