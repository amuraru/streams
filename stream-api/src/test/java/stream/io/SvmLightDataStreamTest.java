package stream.io;

import static org.junit.Assert.fail;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import stream.data.Data;

public class SvmLightDataStreamTest {

	@Test
	public void testReadNextData() throws Exception {

		try {
			URL url = SvmLightDataStreamTest.class.getResource( "/test-data.svm_light" );
			DataStream stream = new SvmLightDataStream( url );

			Data item = stream.readNext();
			while( item != null ){
				Double expTarget = -1.0d;
				Assert.assertEquals( expTarget.doubleValue(), ((Double) item.get( "@label" )).doubleValue(), 0.0001 );
				Assert.assertEquals( new Double( "0.43" ), item.get( "1" ) );
				Assert.assertEquals( new Double( "0.12" ), item.get( "3" ) );
				Assert.assertEquals( new Double( "0.2" ), item.get( "9284" ) );
				item = stream.readNext( item );
			} 

		} catch (Exception e) {
			fail("Error: " + e.getMessage() );
		}
	}
}
