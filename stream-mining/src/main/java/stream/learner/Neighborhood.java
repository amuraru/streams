package stream.learner;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import stream.data.Data;

public class Neighborhood 
	extends HashSet<Data> 
{
	/** The unique class ID */
	private static final long serialVersionUID = -8202064440727754597L;

	Distance distance;
	
	public Neighborhood( Distance dist ){
		this.distance = dist;
	}
	
	
	
	public Set<Data> getNeighbors( Data pivot, int maxNeigh ){
		
		TreeSet<Data> neighs = new TreeSet<Data>( new NeighborDistance( pivot, distance ) );
		neighs.addAll( this );
		
		Set<Data> neighbors = new LinkedHashSet<Data>();
		Iterator<Data> it = neighs.iterator();
		while( it.hasNext() && neighbors.size() < maxNeigh ){
			neighbors.add( it.next() );
		}
		
		return neighbors;
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
			return d1.compareTo( d2 );
		}
	}
}