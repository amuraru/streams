package stream.learner;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;


public class NeighborhoodTest {
	
	final static Distance distance = new stream.learner.EucleadianDistance();
	static Logger log = LoggerFactory.getLogger( NeighborhoodTest.class );
	
	
	@Test
	public void testGetNeighbors() {
		
		Neighborhood hood = new Neighborhood( distance );
		
		Data zero = new DataImpl();
		zero.put( "x", 0.0d );
		zero.put( "y", 0.0d );
		
		for( int i = 1; i < 11; i++ ){
			Double d = new Double( i );
			Data datum = new DataImpl();
			datum.put( "@id", new Integer( i ) );
			datum.put( "x", d );
			datum.put( "y", d );
			hood.add( datum );
		}
		
		int k = 5;

		Set<Data> neighs = hood.getNeighbors( zero, k );
		log.info( "{} neighbors are: {}", k, neighs );
		for( Data neigh : neighs ){
			log.info(   " distance to {} is: {}", neigh, distance.distance( zero, neigh ) );
		}
		Assert.assertEquals( k, neighs.size() );
	}
}