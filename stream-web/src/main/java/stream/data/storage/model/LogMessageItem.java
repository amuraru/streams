/**
 * 
 */
package stream.data.storage.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author chris
 *
 */
@Entity
@Table( name = "LOGS" )
public class LogMessageItem implements Serializable {
	
	/** The unique class ID */
	private static final long serialVersionUID = 4850723658162107595L;

	@Id @GeneratedValue
	Integer id;
	
	@Column( name = "TIMESTAMP" )
	Long timestamp;
	
	@Column( name = "NAME" )
	String name;
	
	@Column( name = "TEXT", length = 4096 )
	String message;

	
	public LogMessageItem(){
		timestamp = System.currentTimeMillis();
	}

	
	public LogMessageItem( String name, String msg ){
		this.timestamp = System.currentTimeMillis();
		this.name = name;
		this.message = msg;
	}
	

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the text
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * @param text the text to set
	 */
	public void setMessage(String text) {
		this.message = text;
	}


	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}


	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	public Date getDate(){
		return new Date( timestamp );
	}
}