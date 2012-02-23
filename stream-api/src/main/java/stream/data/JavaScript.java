/**
 * 
 */
package stream.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.tools.URLUtilities;
import stream.util.Description;
import stream.util.EmbeddedContent;
import stream.util.Parameter;

/**
 * @author chris
 *
 */
@Description( group = "Data Stream.Processing.Script" )
public class JavaScript implements DataProcessor {

	static Logger log = LoggerFactory.getLogger( JavaScript.class );

	final static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	final static String preamble = URLUtilities.readContentOrEmpty( JavaScript.class.getResource( "/stream/data/JavaScript.preamble" ) );
	
	final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName( "JavaScript" );

	
	File file;
	EmbeddedContent embedded = null;


	transient String theScript = null;


	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @param file the file to set
	 */
	@Parameter( required = false )
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @return the embedded
	 */
	public EmbeddedContent getScript() {
		return embedded;
	}


	/**
	 * @param embedded the embedded to set
	 */
	public void setScript(EmbeddedContent embedded) {
		this.embedded = embedded;
	}


	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {

		try {
			String script = loadScript();
			
			if( script == null )
				return data;
			
			log.debug( "Script loaded is:\n{}", script );

			ScriptContext ctx = scriptEngine.getContext();
			scriptEngine.put( "data", data );
			
			log.debug( "Evaluating script..." );
			scriptEngine.eval(script, ctx );

		} catch (Exception e){
			log.error( "Failed to execute script: {}", e.getMessage() );
			if( log.isDebugEnabled() )
				e.printStackTrace();

			throw new RuntimeException( "Script execution error: " + e.getMessage() );
		}

		log.debug( "Returning data: {}", data );
		return data;
	}


	protected String loadScript() throws Exception {

		if( theScript == null ){
			
			if( embedded != null ){
				log.info( "Using embedded content..." );
				theScript = preamble + "\n" + embedded.getContent();
				return theScript;
			}

			if( file != null ){
				log.debug( "Reading script from file {}", file );
				theScript = loadScript( new FileInputStream( file ) );
				return theScript;
			}
		}

		return theScript;
	}





	protected String loadScript( InputStream in ) throws Exception {
		log.debug( "Loading script from input-stream {}", in );
		StringBuffer s = new StringBuffer();
		s.append( preamble );
		s.append( "\n" );
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		String line = reader.readLine();
		while( line != null ){
			s.append( line + "\n" );
			log.debug( "Appending line: {}", line );
			line = reader.readLine();
		}
		reader.close();
		return s.toString();
	}
}