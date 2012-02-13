/**
 * 
 */
package stream.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.vector.VectorMath;
import stream.learner.Distance;

/**
 * This class implements a very simple KMeans-Variant, without any optimizations
 * to find optimal starting points, etc.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class KMeans {

	// A "random" feature for temporal use only
	public final String CLUSTER_KEY = "._cluster_" + System.currentTimeMillis();
	
	/* The logger for this class */
	static Logger log = LoggerFactory.getLogger( KMeans.class );
	

	/** This is the target attribute added to all clustered examples */
	String key = "@cluster";
	
	/** The distance function */
	Distance distanceFunction = new EucleadianDistance();
	
	/** The number of clusters to find */
	Integer k = 2;
	
	/** The number of convergence runs */
	Integer runs = 10;

	final Map<Integer,Data> centroids = new HashMap<Integer,Data>();
	final Map<Integer,List<Data>> clusters = new HashMap<Integer,List<Data>>();

	
	/**
	 * 
	 * 
	 */
	public KMeans(){
		setK( 2 );
	}



	/**
	 * @return the k
	 */
	public Integer getK() {
		return k;
	}


	/**
	 * @param k the k to set
	 */
	public void setK(Integer k) {
		this.k = k;
		clusters.clear();
		for( Integer i = 0; i < k; i++ ){
			clusters.put( i, new ArrayList<Data>() );
		}
	}
	

	/**
	 * @return the runs
	 */
	public Integer getRuns() {
		return runs;
	}


	/**
	 * @param runs the runs to set
	 */
	public void setRuns(Integer runs) {
		this.runs = runs;
	}



	public Collection<Data> process( Collection<Data> input ){

		Integer c = 0;
		List<Data> centroidList = selectRandom( input, k );
		for( Data centroid : centroidList ){
			log.trace( "Cluster-{} centroid is: {}", c, centroid );
			centroids.put( c++, centroid );
		}


		//
		// assign each data point to its nearest centroid
		//
		for( Data point : input ){
			Integer clusterId = getNearest( centroids, point );
			List<Data> cluster = clusters.get( clusterId );
			cluster.add( point );
		}

		//
		// compute the new centroids for each cluster
		//
		for( int run = 0; run < runs; run++ ){
			log.trace( "Convergence-run {}", run );
			
			for( Integer clusterId : clusters.keySet() ){

				List<Data> cluster = clusters.get( clusterId );
				
				double d = 1.0d / cluster.size();
				Data centroid = new DataImpl();
				for( Data point : cluster ){
					VectorMath.add( centroid, point );
				}

				log.trace( "scale: {}", d );
				VectorMath.scale( centroid, d );
				
				log.trace( "new centroid for cluster {} is: {}", clusterId, centroid );
				this.centroids.put( clusterId, centroid );
			}
		}
		
		for( Data point : input ){
			Integer clusterId = this.getNearest( centroids, point );
			point.put( "@cluster", clusterId );
		}

		return input;
	}


	private Integer getNearest( Map<Integer,Data> centroids, Data point ){
		Integer nearest = null;
		double dist = Double.MAX_VALUE;
		log.trace( "---------------------------------------------" );
		log.trace( "Searching nearest cluster for {}", point );
		for( Integer clusterId : centroids.keySet() ){

			Data centroid = centroids.get( clusterId );
			double curDist = distanceFunction.distance( centroid, point );

			log.trace( "cluster: " + clusterId + ", distance to centroid {} is: {}", centroid, curDist );
			
			if( nearest == null ){
				nearest = clusterId;
				dist = curDist;
			} else {
				if( dist > curDist ){
					nearest = clusterId;
					dist = curDist;
				}
			}
		}
		log.trace( "Nearest cluster is {}", nearest );
		log.trace( "---------------------------------------------" );
		return nearest;
	}


	/**
	 * Select k random centroids from the given data collection. If k is larger
	 * than the specified collection, then all data points will be selected.
	 * 
	 * @param input The collection to choose from.
	 * @param k     The number of elements to choose.
	 * @return
	 */
	private List<Data> selectRandom( Collection<Data> input, int k ){
		List<Data> list = new ArrayList<Data>();

		if( input.size() < k ){
			list.addAll( input );
			return list;
		}

		Random rnd = new Random();

		Set<Integer> indexes = new HashSet<Integer>();
		while( indexes.size() < k ){
			Integer idx = rnd.nextInt( input.size() );
			if( ! indexes.contains( idx ) )
				indexes.add( idx );
		}


		Iterator<Data> it = input.iterator();
		Integer idx = 0;
		while( it.hasNext() && list.size() < k ){
			Data centroid = it.next();
			if( indexes.contains( idx ) ){
				list.add( centroid );
			}
			idx++;
		}

		return list;
	}
}