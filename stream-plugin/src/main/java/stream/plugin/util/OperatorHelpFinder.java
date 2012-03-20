/**
 * 
 */
package stream.plugin.util;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.tools.URLUtilities;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author chris
 *
 */
public class OperatorHelpFinder {
	
	static Logger log = LoggerFactory.getLogger( OperatorHelpFinder.class );

	public static String findOperatorHelp( Class<?> clazz ) throws Exception {
		return findOperatorHelp( clazz.getCanonicalName() );
	}
	
	
	public static String findOperatorHelp( String className ) throws Exception {

		String doc = "/" + className.replaceAll( "\\.", "/" ) + ".md";
		URL url = OperatorGenerator.class.getResource( doc );
		if( url != null ){
			String txt = URLUtilities.readContent( url );
			log.info( "Found documentation at {}", url );

			MarkdownProcessor markdown = new MarkdownProcessor();
			String html = markdown.markdown( txt );

			if( html.startsWith( "<h1>" ) ){
				int end = html.indexOf( "</h1>" );
				if( end > 0 ){
					html = html.substring( end + "</h1>".length() );
				}
			}

			log.info( "Html documentation:\n{}", html.trim() );
			return html;
		}
		
		return null;
	}
}
