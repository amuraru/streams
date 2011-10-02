/**
 * 
 */
package stream.data.storage;

import stream.data.storage.model.NoteItem;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author chris
 *
 */
@XStreamAlias( "Note" )
public class Note {

	@XStreamAsAttribute
	String name;
	
	@XStreamAsAttribute
	Long timestamp;
	
	byte[] data;
	
	public Note( NoteItem item ){
		name = item.getName();
		timestamp = item.getCreated().getTime();
		data = item.getText().getBytes();
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

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
}