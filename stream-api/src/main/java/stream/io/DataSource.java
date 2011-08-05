/**
 * 
 */
package stream.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.io.DataStream;

/**
 * @author chris
 *
 */
public class DataSource {
	static Logger log = LoggerFactory.getLogger( DataSource.class );

	String name;

	String url;

	String descriptionRef;

	String className;

	Map<String,String> parameter = new LinkedHashMap<String,String>();

	public DataSource(){
	}


	public DataSource( String name, String url, String className ){
		this.name = name;
		this.url = url;
		this.className = className;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}


	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}


	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}


	public String toString(){
		return "data.source=" + url + "\ndata.source.className=" + className;
	}


	public void setParameter( String key, String value ){
		if( this.parameter == null )
			parameter = new LinkedHashMap<String,String>();
		this.parameter.put( key, value );
	}


	public String getParameter( String key ){
		if( parameter == null )
			return null;

		return parameter.get( key );
	}


	/**
	 * @return the descriptionRef
	 */
	public String getDescriptionRef() {
		return descriptionRef;
	}


	/**
	 * @param descriptionRef the descriptionRef to set
	 */
	public void setDescriptionRef(String descriptionRef) {
		this.descriptionRef = descriptionRef;
	}


	public String retrieveDescription() {
		return retrieveDescription( ".txt" );
	}


	public String retrieveDescription( String ext ) {
		try {
			URL url = resolveUrl( getUrl() + ext );
			if( url == null )
				return "";
			log.info( "  Retrieving data-source information from {}", url );
			URLConnection con = url.openConnection();
			log.info( "  Content-Type is {}", con.getContentType() );
			StringBuffer s = new StringBuffer();

			BufferedReader r = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
			String line = r.readLine();
			while( line != null ){
				s.append( line + "\n" );
				line = r.readLine();
			}
			r.close();
			return s.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public URL resolveUrl( String url ) throws Exception {
		if( url.toLowerCase().startsWith( "classpath:") ){
			log.info( "Found classpath-url: {}", url );
			String resource = url.substring( "classpath:".length() );
			log.info( "   looking for resource '{}'", resource );
			return DataSource.class.getResource( resource );
		} else
			return new URL( getUrl() );
	}


	public DataStream createDataStream() throws Exception {
		Class<?> clazz = Class.forName( className );


		if( url != null && ! url.isEmpty() ){
			Constructor<?> con = clazz.getConstructor( URL.class );
			URL streamURL = resolveUrl( url );

			if( getParameter( "username" ) != null && getParameter( "password" ) != null ){
				Authenticator.setDefault( new Authenticator(){
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication( getParameter("username"), getParameter("password").toCharArray() );
					}
				});
			}

			DataStream stream = (DataStream) con.newInstance( streamURL );
			return stream;
		} else {
			DataStream stream = (DataStream) clazz.newInstance();
			return stream;
		}
	}
}