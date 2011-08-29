package stream.learner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.model.NominalDistributionModel;

public class NearestNeighborClassifier 
	extends AbstractClassifier<Data, String> 
{
	/** The unique class ID */
	private static final long serialVersionUID = 5905212783119820145L;
	
	static Logger log = LoggerFactory.getLogger( NearestNeighborClassifier.class );
	
	String labelAttribute = null;
	List<Data> items = new ArrayList<Data>();
	
	
	/**
	 * @see stream.learner.AbstractClassifier#predict(java.lang.Object)
	 */
	@Override
	public String predict(Data item) {

		if( labelAttribute == null || items.isEmpty() )
			return "?";
		
		TreeSet<Data> neighbors = new TreeSet<Data>( new Distance( item ) );
		neighbors.addAll( items );
		
		NominalDistributionModel<String> dist = new NominalDistributionModel<String>();
		Iterator<Data> it = neighbors.iterator();
		while( it.hasNext() ){
			Data datum = it.next();
			Serializable label = datum.get( labelAttribute );
			dist.update( label.toString() );
		}
		
		return MajorityClass.getMajorityClass( dist );
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
		
		items.add( item );
	}
	
	
	public static class Distance 
		implements Comparator<Data>
	{
		Data pivot;
		
		public Distance( Data pivot ){
			this.pivot = pivot;
		}
		
		public Double distance( Data x1, Data x2 ){
			return 0.0d;
		}

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Data arg0, Data arg1) {
			Double d1 = distance( pivot, arg0 );
			Double d2 = distance( pivot, arg1 );
			return d1.compareTo( d2 );
		}
	}
}