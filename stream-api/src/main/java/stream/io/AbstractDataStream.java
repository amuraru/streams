/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import stream.data.Data;
import stream.data.DataImpl;

/**
 * @author chris
 *
 */
public abstract class AbstractDataStream implements DataStream {

	
	URL url;
	String username;
	String password;
	LinkedHashMap<String,Class<?>> attributes = new LinkedHashMap<String,Class<?>>();
	BufferedReader reader;
	Long limit = -1L;


	public AbstractDataStream( URL url ) throws Exception {
		this.url = url;
		//initReader();
	}
	
	public AbstractDataStream( URL url, String username, String password ) throws Exception {
		this.url = url;
		this.username = username;
		this.password = password;
		this.initReader();
	}
	
	
	protected void initReader() throws Exception {

		if( username != null && password != null ){
			Authenticator.setDefault( new Authenticator(){
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication( username, password.toCharArray() );
				}
			});
		}
		
		if( url.getFile().endsWith( ".gz" ) )
			reader = new BufferedReader( new InputStreamReader( new GZIPInputStream( url.openStream() ) ) );
		else
			reader = new BufferedReader( new InputStreamReader( url.openStream() ) );
		readHeader();
	}

	public AbstractDataStream( InputStream in ) throws Exception {
		reader = new BufferedReader( new InputStreamReader( in ) );
		readHeader();
	}
	
	public Map<String,Class<?>> getAttributes(){
		return attributes;
	}
	
	
	
	
	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
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
	 * 
	 */
	public abstract void readHeader() throws Exception;

	
	/**
	 * @see stream.io.DataStream#readNext()
	 */
	public abstract Data readNext( Data item ) throws Exception;
	
	
	public Data readNext() throws Exception {
		return readNext( new DataImpl() );
	}
}