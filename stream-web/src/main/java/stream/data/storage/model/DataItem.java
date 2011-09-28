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

@Entity
@Table( name = "DATA" )
public class DataItem implements Serializable {

	/** The unique class ID */
	private static final long serialVersionUID = 620590771202483079L;

	@Id
	@Column( name = "ID" )
	String id;
	
	@Lob
	@Column( name = "DATA" )
	byte[] data;

	
	public DataItem(){
	}
	
	public void setId( String id ){
		this.id = id;
	}
	
	public String getId(){
		return id;
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
	}
}