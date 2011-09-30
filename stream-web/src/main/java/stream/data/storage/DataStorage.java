package stream.data.storage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

import stream.data.Data;
import stream.data.storage.model.StatisticsItem;

public class DataStorage {

	static DataStorage storage = null;
	SessionFactory sessionFactory;

	private DataStorage(){
		try {
			Properties p = new Properties();
			
			String dbDriver = "com.mysql.jdbc.Driver";
			//dbDriver = "org.postgresql.Driver";
			String dbUrl = "jdbc:mysql://localhost:3306/DataStorage?autoReconnect=true";
			//dbUrl = "jdbc:postgresql://localhost:5432/DataStorage";
			String dbUser = "datanode";
			String dbPass = "datanode";
			
			p.setProperty( "hibernate.connection.driver_class", dbDriver );
			p.setProperty( "hibernate.connection.url", dbUrl );
			p.setProperty( "hibernate.connection.username", dbUser );
			p.setProperty( "hibernate.connection.password", dbPass );
			p.setProperty( "hibernate.hbm2ddl.auto", "update" );
			if( "true".equals( System.getProperty( "hibernate.debug" ) ) ){
				p.setProperty( "hibernate.show_sql", "true" );
			}

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
			.addAnnotatedClass( stream.data.storage.model.StatisticsItem.class )
			.addAnnotatedClass( stream.data.storage.model.NoteItem.class )
			.setProperties( p )
			.setNamingStrategy( new DefaultComponentSafeNamingStrategy() );

			sessionFactory = cfg.configure().buildSessionFactory();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected SessionFactory getSessionFactory(){
		return sessionFactory;
	}

	public static DataStorage getDataStorage(){
		if( storage == null )
			storage = new DataStorage();
		return storage;
	}

	public void store( Data item ) throws StorageException {
		throw new StorageException( "Storage not available!" );
	}



	public Set<String> getStatisticKeys() throws StorageException {
		Set<String> keys = new TreeSet<String>();

		Session s = sessionFactory.openSession();
		List<?> list = s.createQuery( "select distinct s.key from StatisticsItem s" ).list();
		for( Object o : list ){
			keys.add( o.toString() );
		}
		s.close();

		return keys;
	}



	public void addStatistics( String key, Data item ) throws StorageException {
		Transaction tx = null;
		Session s = sessionFactory.openSession();
		try {
			StatisticsItem stat = new StatisticsItem( item );
			stat.setKey( key );
			tx = s.beginTransaction();
			s.saveOrUpdate( stat );
			tx.commit();
			s.close();
		} catch (Exception e) {
			if( tx != null )
				tx.rollback();
			s.close();
			throw new StorageException( "Failed to store statistics for key '" + key + "': " + e.getMessage() );
		}
	}

	public List<Data> getStatistics( String key ) throws StorageException {
		List<Data> results = new ArrayList<Data>();

		Session s = sessionFactory.openSession();
		List<?> list = s.createQuery( "from StatisticsItem s where s.key = ? order by s.timestamp asc" ).setParameter(0, key).list();
		for( Object o : list ){

			try {
				StatisticsItem stats = (StatisticsItem) o;
				Data data = (Data) stats.getObject();
				results.add( data );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		s.close();

		return results;
	}
	
	public Session openSession(){
		return sessionFactory.openSession();
	}
}