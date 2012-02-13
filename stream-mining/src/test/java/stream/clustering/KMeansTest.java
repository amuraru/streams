/**
 * 
 */
package stream.clustering;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.mapper.KeyMapper;
import stream.data.util.ZNormalization;
import stream.io.CsvStream;

/**
 * @author chris
 *
 */
public class KMeansTest {
	
	static Logger log = LoggerFactory.getLogger( KMeansTest.class );
	
	
	public static List<Data> createNoiseCloud( Data point, double range, int size ){
		
		List<Data> cloud = new ArrayList<Data>();
		cloud.add( point );
		Random rnd = new Random();

		
		for( int i = 0; i < size; i++ ){
		
			Data dat = new DataImpl();
			
			for( String key : point.keySet() ){
				
				Serializable val = point.get( key );
				if( val instanceof Double ){
					Double d = (Double) val;
					dat.put( key, d + range * rnd.nextDouble() );
				} else {
					dat.put( key, val );
				}
			}
			
			cloud.add( dat );
		}
		
		return cloud;
	}
	
	
	public static List<Data> readCsv( String name, int limit ) throws Exception {
		List<Data> examples = new ArrayList<Data>();
		
		URL url = KMeansTest.class.getResource( name );
		CsvStream stream = new CsvStream( url );

		KeyMapper mapper = new KeyMapper();
		mapper.setNew( "@id" );
		mapper.setOld( "id" );
		
		Data item = stream.readNext();
		int i = 0;
		while( item != null && i++ < limit ){
			examples.add( mapper.process( item ) );
			item = stream.readNext();
		}
		
		return examples;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		KMeans kmeans = new KMeans();
		kmeans.setK( 2 );
		kmeans.setRuns( 100 );
		
		List<Data> examples = new ArrayList<Data>(); //
		
		Data c1 = new DataImpl();
		c1.put( "@label", "Cloud-1" );
		c1.put( "x", 1.0d );
		c1.put( "y", 1.0d );
		examples.addAll( createNoiseCloud( c1, 1.0d, 50 ) );

		Data c2 = new DataImpl();
		c2.put( "@label", "Cloud-2" );
		c2.put( "x", 5.0d );
		c2.put( "y", 5.0d );
		examples.addAll( createNoiseCloud( c2, 1.0d, 50 ) );

		ZNormalization.normalize( examples );
		Collections.shuffle( examples );
		
		// test on Iris:
		//
		//examples = readCsv( "/iris-binary-shuffled.csv", 1000 );
		
		long time = System.currentTimeMillis();
		kmeans.process( examples );
		long dur = System.currentTimeMillis() - time;
		log.info( "kMeans required {} ms", dur );
		
		
		for( Data item : examples ){
			log.info( "{}", item );
		}
	}
}