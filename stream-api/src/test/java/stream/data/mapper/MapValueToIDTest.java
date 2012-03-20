/**
 * 
 */
package stream.data.mapper;

import junit.framework.Assert;

import org.junit.Test;

import stream.data.Data;
import stream.data.DataImpl;

/**
 * @author chris
 *
 */
public class MapValueToIDTest {

	/**
	 * Test method for {@link stream.data.mapper.MapValueToID#process(stream.data.Data)}.
	 */
	@Test
	public void testProcess() {
		
		MapValueToID mapper = new MapValueToID();
		mapper.setKey( "f1" );
		
		
		Data item = new DataImpl();
		item.put( "f1", "ABC" );
		item.put( "f2", "DEF" );
		
		item = mapper.process( item );
		Assert.assertEquals( "1", item.get( "f1" ).toString() );
		
		item.put( "f1", "GHI" );
		item = mapper.process( item );
		Assert.assertEquals( "2", item.get( "f1" ).toString() );
	}
}
