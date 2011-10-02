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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author chris
 *
 */
@Entity
@Table( name = "NOTES" )
@XStreamAlias( "Note" )
public class NoteItem implements Serializable {
	
	/** The unique class ID */
	private static final long serialVersionUID = 4850723658162107595L;

	@XStreamAsAttribute
	@Id @GeneratedValue
	Integer id;
	
	
	@XStreamAsAttribute
	@Column( name = "NAME" )
	String name;
	
	@Column( name = "TEXT", columnDefinition = "TEXT" )
	String text;

	@XStreamAsAttribute
	@Column( name = "CREATED")
	Date created;
	
	
	public NoteItem(){
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
	public String getText() {
		return text;
	}


	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}


	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}


	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
}