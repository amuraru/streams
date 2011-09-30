/**
 * 
 */
package stream.web.services;

import stream.data.storage.model.User;

/**
 * @author chris
 *
 */
public interface UserService {

	public User authenticate( String user, String password );
}
