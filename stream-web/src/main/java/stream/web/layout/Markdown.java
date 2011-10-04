/**
 * 
 */
package stream.web.layout;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.web.SciNotesContext;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author chris
 *
 */
public class Markdown {

	static Logger log = LoggerFactory.getLogger( Markdown.class );
	public final static String PDFLATEX = "/sw/bin/pdflatex";
	public final static String CONVERT = "/usr/local/bin/convert";

	public final static String LATEX_BEGIN = 
			"\\documentclass[a4,12pt]{article}\n"
					+ "\\begin{document}\n"
					+ "\\pagestyle{empty}\n"
					+ "\\begin{displaymath}\n"
					+ "";

	public final static String LATEX_END =
			"\n"
					+ "\\end{displaymath}\n"
					+ "\\end{document}";



	public static String read( InputStream in ){
		try {
			StringBuffer s = new StringBuffer();
			BufferedReader r = new BufferedReader( new InputStreamReader( in ) );
			String line = r.readLine();
			while( line != null ){
				s.append( line + "\n" );
				line = r.readLine();
			}
			return s.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String compile( URL url ){
		try {
			return compile( url.openStream() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String compile( InputStream in ){
		return compile( read( in ) );
	}


	public static String compile( String text ){
		log.trace( "Compiling:\n{}", text );
		String src = text;
		MarkdownProcessor p = new MarkdownProcessor();
		LatexMathCompiler lmc = new LatexMathCompiler();
		
		Map<String,String> subs = new LinkedHashMap<String,String>();

		if( lmc.isComplete() ){

			for( String delim : new String[]{ "$$", "$" } ){
				int idx = src.indexOf( delim );
				try {
					while( idx >= 0 ){
						int end = src.indexOf( delim, idx + delim.length() );
						if( end >= idx ){
							try {
								String form = src.substring( idx + delim.length(), end );
								log.debug( "Need to compile formular '{}'", form );
								String ref = lmc.compileFormular( form, SciNotesContext.getImageDirectory() );

								if( "$$".equals( delim ) )
									subs.put( ref, createFormularHtml( ref ) );
								else
									subs.put( ref, createInlineFormularHtml( ref ) );
								src = src.substring( 0, idx ) + ref + src.substring( end + delim.length() );
								log.trace( "html text after substitution:\n{}", src );
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						idx = src.indexOf( "$$", end + delim.length() );
						while( idx >= 1 && src.indexOf( idx - 1 ) == '\\' ){
							idx = src.indexOf( "$$", end + delim.length() );
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			log.error( "Some requirements are not satisfied, please check your LaTeX and your ImageMagix setup!" );
		}

		log.debug( "My compiled substitutions: {}", subs );
		String html = p.markdown( src );
		for( String key : subs.keySet() ){
			html = html.replace( key, subs.get( key ) );
		}
		return html;
	}

	public void compileFormulars( String text, File outputDirectory ){

	}

	public static String createFormularHtml( String ref ){
		String img = "<img class=\"equation\" src=\"" + SciNotesContext.getImageBaseUrl() + "/" + ref + ".png\"/>";
		log.debug( "Compiled form to image {}", img );
		String div = "<p class=\"equation\">" + img + "</p>"; 
		return div;
	}

	public static String createInlineFormularHtml( String ref ){
		String img = "<img class=\"inlineEquation\" src=\"" + SciNotesContext.getImageBaseUrl() + "/" + ref + ".png\"/>";
		log.debug( "Compiled form to image {}", img );
		String div = "<span class=\"inlineEequation\">" + img + "</span>";
		return div;
	}


	public static String compileFormular( String form, File outputDirectory ) throws Exception {
		LatexMathCompiler lmc = new LatexMathCompiler();
		return lmc.compileFormular( form, outputDirectory );
	}
}