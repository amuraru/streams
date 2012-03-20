/**
 * 
 */
package stream.data;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.junit.Assert;
import org.junit.Test;

import stream.util.EmbeddedContent;

/**
 * @author chris
 *
 */
public class JRubyTest {

	@Test
	public void test() {

		try {
			
			ScriptEngineManager manager = new ScriptEngineManager();
			
			List<ScriptEngineFactory> factories = manager.getEngineFactories();
			for( ScriptEngineFactory f : factories ){
				System.out.println( "" + f.getEngineName() + ", lang=" + f.getLanguageName() + ", version=" + f.getLanguageVersion() );
			}
			
			ScriptEngine engine = manager.getEngineByName( "ruby" );
			if( engine == null )
				throw new RuntimeException( "Failed to initialize ScriptEngine for JRuby!" );
			
			JRuby jython = new JRuby();
			
			StringBuffer script = new StringBuffer();
			script.append( "puts \"test\"; \n" );
			script.append( "$data.put( \"key\", \"value\" );" );
			
			jython.setScript( new EmbeddedContent( script.toString() ) );
	
			Data item = new DataImpl();
			item = jython.process( item );
			
			Assert.assertEquals( "value", item.get( "key" ) );
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Not yet implemented");
		}
	}

}
