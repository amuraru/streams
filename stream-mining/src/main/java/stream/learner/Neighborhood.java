package stream.learner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

public class Neighborhood 
extends ArrayList<Data> 
{
	/** The unique class ID */
	private static final long serialVersionUID = -8202064440727754597L;
	static Logger log = LoggerFactory.getLogger( Neighborhood.class );

	Distance distance;

	public Neighborhood( Distance dist ){
		this.distance = dist;
	}



	public Set<Data> getNeighbors( Data pivot, int maxNeigh ){

		List<Data> neighs = new ArrayList<Data>( this );
		Collections.sort( neighs, new NeighborDistance( pivot, distance ) );

		log.info( " {} neighbors sorted to {} ones", this.size(), neighs.size() );
		
		if( log.isDebugEnabled() ){
			log.debug( "Found total of {} neighbors", neighs.size() );
			for( Data item : neighs ){
				log.debug( "{}  =>  {}", distance.distance( pivot, item ), item );
			}
		}
		
		LinkedHashSet<Data> result = new LinkedHashSet<Data>();
		for( int i = 0; i < neighs.size() && i < maxNeigh; i++ )
			result.add( neighs.get(i) );
		return result;
	}



	public class NeighborDistance implements Comparator<Data> {
		Data pivot;
		Distance dist;

		public NeighborDistance( Data pivot, Distance dist ){
			this.pivot = pivot;
			this.dist = dist;
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Data arg0, Data arg1) {
			Double d1 = dist.distance( pivot, arg0 );
			Double d2 = dist.distance( pivot, arg1 );
			
			int rc = d1.compareTo( d2 );
			if( rc == 0 )
				return arg0.toString().compareTo( arg1.toString() );
			return rc;
		}
	}
}