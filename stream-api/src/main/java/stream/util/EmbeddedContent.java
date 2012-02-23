/**
 * 
 */
package stream.util;

/**
 * @author chris
 *
 */
public class EmbeddedContent {

	public final static String KEY = "__EMBEDDED_CONTENT__";
	String content;
	
	public EmbeddedContent( String txt ){
		content = txt;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
}