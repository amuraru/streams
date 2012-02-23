/**
 * 
 */
package stream.plugin.test;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.plugin.util.ParameterTypeDiscovery;

import com.rapidminer.parameter.ParameterType;

/**
 * @author chris
 *
 */
public class ParameterDiscoveryTest {

	static Logger log = LoggerFactory.getLogger( ParameterDiscoveryTest.class );
	
	@Test
	public void test() {
		
		Map<String,ParameterType> types = ParameterTypeDiscovery.discoverParameterTypes( stream.io.LogDataStream.class );
		for( String key : types.keySet() ){
			log.info( "{} => {}", key, types.get( key ) );
		}
		//fail("Not yet implemented");
	}
}
