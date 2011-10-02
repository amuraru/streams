/**
 * 
 */
package stream.web.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import stream.data.storage.DataStorage;
import stream.data.storage.model.LogMessageItem;

/**
 * @author chris
 *
 */
public class LogServiceImpl implements LogService {

	/**
	 * @see stream.web.services.LogService#log(java.lang.Integer, java.lang.String, java.lang.String)
	 */
	@Override
	public void log(Integer level, String tag, String message) {
		Transaction tx = null;
		Session s = openSession();
		try {
			
			LogMessageItem lm = new LogMessageItem( tag, message );
			tx = s.beginTransaction();
			s.save( lm );
			tx.commit();
			
		} catch (Exception e) {
			if( tx != null )
				tx.rollback();
			
		} finally {
			s.close();
		}
	}

	/**
	 * @see stream.web.services.LogService#getLogs(java.lang.String, int, int)
	 */
	@Override
	public List<LogMessageItem> getLogs(String tag, int offseet, int limit) {
		
		List<LogMessageItem> msgs = new ArrayList<LogMessageItem>();
		Session s = openSession();

		try {
			Query query;
			
			if( tag != null )
				query = s.createQuery( "from LogMessageItem m where m.name = ?" ).setParameter( 0, tag );
			else
				query = s.createQuery( "from LogMessageItem m" );
			
			List<?> list = query.setFirstResult( offseet ).setMaxResults( limit ).list();
			for( Object o : list ){
				LogMessageItem lm = (LogMessageItem) o;
				msgs.add( lm );
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			s.close();
		}

		return msgs;
	}
	
	
	protected Session openSession(){
		return DataStorage.getDataStorage().openSession();
	}
}