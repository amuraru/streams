package stream.learner;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.CsvStream;

public class PerceptronTest {
	static Logger log = LoggerFactory.getLogger( PerceptronTest.class );

	@Test
	public void test() throws Exception {

		URL url = PerceptronTest.class.getResource( "/iris-binary-shuffled.csv" );
		CsvStream stream = new CsvStream( url );

		Perceptron p = new Perceptron();
		int i = 0;
		int trainErrors = 0;
		List<Data> dataset = new ArrayList<Data>();
		Data item = stream.readNext();
		while( item != null ){
			dataset.add( item );
			if( i > 0 ){
				String prediction = p.predict( item );
				log.info( "------------------------------------------------" );
				log.info( "@label: {}", item.get( "@label" ) );
				log.info( "@pred: {}", prediction );
				log.info( "------------------------------------------------" );

				if( !prediction.equals( item.get( "@label" ).toString() ) ){
					trainErrors++;
				}
			}

			p.learn( item );
			i++;

			item = stream.readNext();
		}

		log.info( "Perceptron produced {} errors during training.", trainErrors );
		log.info( "Model:\n{}", p.getModel() );
		
		trainErrors = 0;
		for( Data example : dataset ){
			String prediction = p.predict( example );
			log.info( "------------------------------------------------" );
			log.info( "@label: {}", example.get( "@label" ) );
			log.info( "@pred: {}", prediction );
			log.info( "------------------------------------------------" );

			if( !prediction.equals( example.get( "@label" ).toString() ) ){
				trainErrors++;
			}
		}

		log.info( "Perceptron produced {} errors after complete training.", trainErrors );
		log.info( "Model:\n{}", p.getModel().toString() );

	}
}
