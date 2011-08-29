package stream.learner;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;

public class EucleadianDistanceTest {

	static Logger log = LoggerFactory.getLogger( EucleadianDistanceTest.class );
	Data x1;
	Data x2;
	
	
	@Before
	public void setUp(){
		x1 = new DataImpl();
		x1.put( "x", 1.0d );
		
		x2 = new DataImpl();
		x2.put( "y", 2.0d );
	}
	
	
	@Test
	public void testDistance() {
		Distance dist = new EucleadianDistance();
		Double d = dist.distance( x1, x2 );
		log.info( "distance( x1, x2 ) = {}", d );
	}
}
