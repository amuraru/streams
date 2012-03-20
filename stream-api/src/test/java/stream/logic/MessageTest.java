/**
 * 
 */
package stream.logic;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.RandomStream;

/**
 * @author chris
 *
 */
public class MessageTest {

	static Logger log = LoggerFactory.getLogger( MessageTest.class );
	
	@Test
	public void test() throws Exception {
		
		RandomStream stream = new RandomStream( 10000L );
		stream.getAttributes().put( "x1", Double.class );
		stream.getAttributes().put( "x2", Double.class );
		

		int i = 0;
		Message m = new Message();
		m.setTxt( "%{x1} ist kleiner als 0.5 und größer als 0.1" );
		m.setCondition( "x1 @lt 0.5  and  x1 @ge 0.1" );
		
		Data item = stream.readNext();
		while( item != null && i++ < 10 ){
			m.process( item );
			item = stream.readNext();
		}
		
		fail("Not yet implemented");
	}
}
