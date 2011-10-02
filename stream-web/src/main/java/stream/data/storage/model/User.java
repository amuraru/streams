/**
 * 
 */
package stream.data.storage.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author chris
 *
 */
@Entity
@Table( name = "USERS" )
@XStreamAlias( "User" )
public class User implements Serializable {
	
	/** The unique class ID */
	private static final long serialVersionUID = 4850723658162107595L;

	@Id
	@Column( name = "USERNAME" )
	@XStreamAsAttribute
	String login;
	
	@Column( name = "PASSWORD" )
	@XStreamAsAttribute
	String password;
	
	@ElementCollection( fetch = FetchType.EAGER )
	@CollectionTable( name = "PERMISSIONS" )
	Set<String> permissions;

	
	public User(){
		permissions = new HashSet<String>();
	}
	
	
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the permissions
	 */
	public Set<String> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
}