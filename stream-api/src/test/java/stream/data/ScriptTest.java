/**
 * 
 */
package stream.data;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.tools.StreamRunner;

/**
 * @author chris
 *
 */
public class ScriptTest {

	static Logger log = LoggerFactory.getLogger( ScriptTest.class );

	@Test
	public void test() {

		try {
			URL url = StreamRunner.class.getResource( "/script-example.xml" );
			log.info( "Running experiment from {}", url );
			StreamRunner runner = new StreamRunner( url );
			runner.run();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Not yet implemented");
		}
	}
}