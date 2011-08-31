package stream.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataUtils;
import stream.data.mapper.HideFeature;

public class ParameterDiscoveryTest {

	static Logger log = LoggerFactory.getLogger( ParameterDiscoveryTest.class );

	@Test
	public void testDiscover() {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put( "startId", new Long( 100L ) );
		params.put( "key", "x" );
		HideFeature proc = new HideFeature();

		try {
			
			Map<String,Class<?>> keys = ParameterDiscovery.discoverParameters( proc.getClass() );
			log.info( "Discovered parameters: {}", keys );
			
			ParameterInjection.inject( proc, params );

			Data datum = new DataImpl();
			datum.put( "x", 1.0d );
			datum.put( "y", 2.10d);

			log.info( "Initial datum: {}", datum );
			datum = proc.process( datum );
			log.info( "Processed datum: {}", datum );

			for( String key : datum.keySet() ){
				if( key.endsWith( "x" ) )
					Assert.assertTrue( DataUtils.isHidden( key ) );
			}
			
		} catch (Exception e) {
			Assert.fail( "Failed: " + e.getMessage() );
		}
	}
}