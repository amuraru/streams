/**
 * 
 */
package stream.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author chris
 *
 */
public class SciNotesContext {

	public static String staticNote( String key ){
		
		try {
			InputStream in = SciNotesContext.class.getResourceAsStream( "/" + key );
			BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
			StringBuffer s = new StringBuffer();
			String line = reader.readLine();
			while( line != null ){
				s.append( line + "\n" );
				line = reader.readLine();
			}
			reader.close();
			
			MarkdownProcessor md = new MarkdownProcessor();
			return md.markdown( s.toString() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
