/**
 * 
 */
package stream.web.layout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.MD5;

/**
 * @author chris
 *
 */
public class LatexMathCompiler 
	extends MarkdownPreprocessor
{

	static Logger log = LoggerFactory.getLogger( LatexMathCompiler.class );

	public final static String PDFLATEX = "/sw/bin/pdflatex";
	public final static String CONVERT = "/usr/local/bin/convert";

	public final static String[] SEARCH_PATHS = {
		"/bin", "/usr/bin", "/usr/local/bin", "/opt/local/bin", "/sw/bin"
	};

	public final static String LATEX_BEGIN = 
			"\\documentclass[a4,10pt]{article}\n"
					+ "\\begin{document}\n"
					+ "\\pagestyle{empty}\n"
					+ "\\begin{displaymath}\n"
					+ "";

	public final static String LATEX_END =
			"\n"
					+ "\\end{displaymath}\n"
					+ "\\end{document}";


	String convert = null;
	String pdflatex = null;


	public LatexMathCompiler(){
		super( "$$", "$$" );
		
		if( convert == null ){
			convert = findExecutable( SEARCH_PATHS, "convert" );
		}
		
		if( pdflatex == null ){
			pdflatex = findExecutable( SEARCH_PATHS, "pdflatex" );
		}
	}

	
	public boolean isComplete(){
		return convert != null && pdflatex != null;
	}
	

	public String findExecutable( String[] paths, String cmd ){
		for( String path : paths ){
			File f = new File( path + File.separator + cmd );
			if( f.isFile() ){
				if( f.canExecute() ){
					log.debug( "Found command '{}' at '{}'", cmd, f.getAbsolutePath() );
					return f.getAbsolutePath();
				} else {
					log.error( "Not allowed to execute {}", f.getAbsolutePath() );
				}
			}
		}
		
		return null;
	}



	public String compileFormular( String form, File outputDirectory ) throws Exception {
		try {
			String digest = MD5.md5( form );
			log.debug( "Compiling {} to {}", form, outputDirectory );
			File pngFile = new File( outputDirectory.getAbsolutePath() + File.separator + digest + ".png" );
			if( pngFile.exists() ){
				log.debug( "Formular already compiled to file {}", pngFile.getAbsolutePath() );
				return digest;
			}

			long time = System.currentTimeMillis();
			File pwd = new File( "." );
			File texFile = File.createTempFile( "formular", ".tex" ); // new File( "/tmp/formular.tex" );
			FileWriter texWriter = new FileWriter( texFile );
			texWriter.write( LATEX_BEGIN );
			texWriter.write( form + "" );
			texWriter.write( LATEX_END );
			texWriter.close();

			File pdfFile = new File( pwd.getAbsolutePath() + File.separator + texFile.getName().replace( ".tex", ".pdf" ) );
			File auxFile = new File( pwd.getAbsolutePath() + File.separator + texFile.getName().replace( ".tex", ".aux" ) );
			File logFile = new File( pwd.getAbsolutePath() + File.separator + texFile.getName().replace( ".tex", ".log" ) );

			String exec = pdflatex + "  -output-directory " + pwd.getAbsolutePath() + " " + texFile.getAbsolutePath();
			log.debug( "Compiling file: {}", exec );
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec( exec );
			
			if( p.exitValue() != 0 ){
				log.error( "Failed to compile tex-file!" );
				dumpErrors( p );
				throw new Exception( "Failed to compile tex-file!" );
			}
			
			if( log.isDebugEnabled() )
				dump( p );
			
			exec = convert + " -verbose -density 150 -trim " + pdfFile.getAbsolutePath() + " -quality 100 -sharpen 0x1.0 " + pngFile.getAbsolutePath();
			log.debug( "Converting to PNG: {}", exec );
			p = rt.exec( exec );
			if( log.isDebugEnabled() )
				dump( p );
			log.debug( "Formular stored in {}", pngFile.getAbsolutePath() );
			
			long end = System.currentTimeMillis();
			log.info( "Compiling latex-formular '{}' took {} ms", form, (end-time) );
			
			//
			// cleanup
			//
			texFile.delete();
			pdfFile.delete();
			auxFile.delete();
			logFile.delete();
			
			return MD5.md5( form );

		} catch (Exception e) {
			e.printStackTrace();
		}
		return form;
	}

	public static void dump( Process p ){
		try {
			BufferedReader r = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String line = r.readLine();
			while( line != null ){
				log.debug("Output: {}", line );
				line = r.readLine();
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	public static void dumpErrors( Process p ){
		try {
			BufferedReader r = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
			String line = r.readLine();
			while( line != null ){
				log.error( "Error: {}", line );
				line = r.readLine();
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @see stream.web.layout.MarkdownPreprocessor#compile(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Substitution compile(String source, String start, String end) {
		return null;
	}
}