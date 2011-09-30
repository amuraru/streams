/**
 * 
 */
package stream.web.services;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.storage.DataStorage;
import stream.data.storage.model.User;
import stream.util.MD5;

/**
 * @author chris
 *
 */
public class UserServiceImpl implements UserService {

	static Logger log = LoggerFactory.getLogger( UserServiceImpl.class );
	
	public UserServiceImpl(){
		Session s = null;
		try {
			
			s = DataStorage.getDataStorage().openSession();
			User admin = (User) s.get( User.class, "admin" );
			if( admin == null ){
				log.debug( "Creating non-existing 'admin' user" );
				admin = new User();
				admin.setLogin( "admin" );
				admin.setPassword( MD5.md5( "admin" ) );
				Transaction tx = s.beginTransaction();
				s.save( admin );
				tx.commit();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see stream.web.services.UserService#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public User authenticate(String login, String password) {
		
		User authenticated = null;
		DataStorage storage = DataStorage.getDataStorage();
		Session s = storage.openSession();
		
		try {
			
			User user = (User) s.load( User.class, login );
			if( user == null )
				return null;
			
			String hashedPassword = MD5.md5( password );
			if( user.getPassword().equals( hashedPassword ) ){
				authenticated = user;
			}
			
		} catch (Exception e) {
			
		} finally {
			s.close();
		}
		
		return authenticated;
	}
}