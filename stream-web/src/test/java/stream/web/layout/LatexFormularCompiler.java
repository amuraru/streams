/**
 * 
 */
package stream.web.layout;

import java.io.File;
import java.io.FileWriter;

/**
 * @author chris
 *
 */
public class LatexFormularCompiler {
	
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main( String[] args ) throws Exception {
		
		String html = Markdown.compile( LatexFormularCompiler.class.getResource( "/test.md" ) );
		FileWriter w = new FileWriter( "/tmp/test/test.html" );
		w.write( html );
		w.close();
		
		Markdown.compileFormular( "f(x) = 3 x_i \\beta_0", new File( "/tmp" ) );
	}
}
