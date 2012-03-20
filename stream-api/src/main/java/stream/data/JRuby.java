/**
 * 
 */
package stream.data;

import javax.script.ScriptEngineManager;

/**
 * @author chris
 *
 */
//@Description( name = "(J)Ruby Script" )
public class JRuby extends Script {
	
	final static ScriptEngineManager engineManager = new ScriptEngineManager();

	public JRuby(){
		super( engineManager.getEngineByName( "ruby" ) );
	}
}