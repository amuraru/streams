package stream.learner;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

public class NearestNeighborRegressor 
	extends AbstractRegressor<Data>
{
	/** The unique class ID */
	private static final long serialVersionUID = 5905212783119820145L;
	
	static Logger log = LoggerFactory.getLogger( NearestNeighborRegressor.class );
	
	String labelAttribute = null;
	//List<Data> items = new ArrayList<Data>();
	Neighborhood instances = new Neighborhood( new EucleadianDistance() );
	
	Integer k = 3; //Integer.MAX_VALUE;
	
	public NearestNeighborRegressor(){
		this( 3 );
	}
	
	public NearestNeighborRegressor( int k ){
		this( new EucleadianDistance(), k );
	}
	
	
	public NearestNeighborRegressor( Distance dist, int k ){
		instances = new Neighborhood( dist );
		this.k = k;
	}
	
	
	public Integer getK() {
		return k;
	}


	public void setK(Integer k) {
		this.k = k;
	}

	public void setDistance( Distance distance ){
		instances = new Neighborhood( distance );
	}
	
	
	public String getLabelAttribute() {
		return labelAttribute;
	}


	public void setLabelAttribute(String labelAttribute) {
		this.labelAttribute = labelAttribute;
	}


	/**
	 * @see stream.learner.AbstractClassifier#predict(java.lang.Object)
	 */
	@Override
	public Double predict(Data item) {

		if( labelAttribute == null || instances.isEmpty() )
			return Double.NaN;
		
		//TreeSet<Data> neighbors = new TreeSet<Data>( new EucleadianDistance( item ) );
		//neighbors.addAll( items );
		Set<Data> neighbors = instances.getNeighbors( item, k );
		log.info( "Using up to {} neighbors for prediction", k );
		log.info( "{} Neighbors for {}", neighbors.size(), item );
		
		Double total = 0.0d;
		int i = 0;
		for( Data neigh : neighbors ){
			Double nd = instances.distance.distance( item, neigh );
			if( i < k )
				total += nd;
			log.info( "  {}   dist: {}", neigh.get( "@id" ), nd );
		}
		
		Integer count = 0;
		Double sum = 0.0d;
		Double norm = 0.0d;
		Iterator<Data> it = neighbors.iterator();
		while( it.hasNext() && count < k ){
			Data datum = it.next();
			Double label = LearnerUtils.getDouble( labelAttribute, datum );
			if( label != null && ! Double.isNaN( label ) ){
				count++;
				double weight = total - instances.distance.distance( item, datum );
				if( Double.isNaN( weight ) )
					weight = 1.0d / k;
				
				if( weight == 0.0d )
					weight = 1.0d;
				
				norm += weight;
				sum += (label * weight);
			}
		}
		
		if( count == 0.0d )
			return Double.NaN;
		
		if( norm == 0.0d )
			return sum;
		
		return sum / norm; //	 / count.doubleValue();
	}
	

	/**
	 * @see stream.learner.AbstractClassifier#learn(java.lang.Object)
	 */
	@Override
	public void learn(Data item) {
		
		if( labelAttribute == null ){
			labelAttribute = LearnerUtils.detectLabelAttribute( item );
			if( labelAttribute == null ){
				log.error( "Failed to detect label for item: {}", item );
			}
		}
		
		if( labelAttribute == null || item.get( labelAttribute ) == null ){
			log.warn( "Ignoring item !" );
			return;
		}
		
		instances.add( item );
	}
	
	
	public String toString(){
		return "kNNreg:" + labelAttribute;
	}
	/*
	
	public static class Distance 
		implements Comparator<Data>
	{
		Data pivot;
		
		public Distance( Data pivot ){
			this.pivot = pivot;
		}
		
		public Double distance( Data x1, Data x2 ){
			
			Set<String> keys = LearnerUtils.getNumericAttributes( x1 );
			keys.addAll( LearnerUtils.getNumericAttributes( x2 ) );
			Double sum = 0.0d;
			
			for( String key : keys ){
				
				if( ! DataUtils.isAnnotation( key ) ){
				Double d1 = LearnerUtils.getDouble( key, x1 );
				if( d1 == null )
					d1 = 0.0d;
				
				Double d2 = LearnerUtils.getDouble( key, x2 );
				if( d2 == null )
					d2 = 0.0d;
				
				sum += ((d1 - d2) * (d1 - d2));
				}
			}
			
			return Math.sqrt( sum );
		}

		@Override
		public int compare(Data arg0, Data arg1) {
			Double d1 = distance( pivot, arg0 );
			Double d2 = distance( pivot, arg1 );
			return d1.compareTo( d2 );
		}
	}
	 */
}