package stream.data.storage.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import stream.data.Data;
import stream.util.MD5;

@Entity
@Table( name = "STATISTICS" )
public class StatisticsItem implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 620590771202483079L;

	@Id
	@Column( name = "ID" )
	String id;

	@Column( name = "NAME" )
	String key;
	
	@Column( name = "TIMESTAMP" )
	Long timestamp = System.currentTimeMillis();

	@Lob
	@Column( name = "DATA" )
	byte[] data;


	public StatisticsItem(){
		this.timestamp = System.currentTimeMillis();
	}


	public StatisticsItem( Data dat ) throws Exception {
		this.setObject( dat );
		this.timestamp = System.currentTimeMillis();
	}


	public void setId( String id ){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
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


	public byte[] getData(){
		return data;
	}

	public void setData( byte[] data ){
		this.data = data;
	}


	public Serializable getObject() throws Exception {
		if( data == null || data.length == 0 )
			return null;

		ByteArrayInputStream bais = new ByteArrayInputStream( data );
		ObjectInputStream in = new ObjectInputStream( bais );
		Serializable object = (Serializable) in.readObject();
		in.close();
		return object;
	}


	public void setObject( Serializable ob ) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( out );
		oos.writeObject( ob );
		oos.close();
		data = out.toByteArray();
		id = null;

		if( ob instanceof Data ){
			Data dat = (Data) ob;
			if( dat.get( "@id" ) != null ){
				id = dat.get( "@id" ).toString();
			}
		}

		if( id == null ){
			id = MD5.md5( data );
		}
	}
}