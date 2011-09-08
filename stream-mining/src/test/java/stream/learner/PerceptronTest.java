package stream.learner;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.mapper.BinaryLabels;
import stream.data.mapper.MapValues;
import stream.io.CsvStream;

public class PerceptronTest {
    static Logger log = LoggerFactory.getLogger( PerceptronTest.class );

    
    static Map<String,Double> labelMapping = new HashMap<String,Double>();
    
    
    public static Double map( Serializable val ){
        
        if( val == null )
            return null;
        
        Double d = labelMapping.get( val.toString() );
        if( d != null )
            return d;
        
        return -1.0d;
    }
    
    
    
    @Test
    public void test() throws Exception {

        try {
            URL url = PerceptronTest.class.getResource( "/iris-binary-shuffled.csv" );
            CsvStream stream = new CsvStream( url );

            
            labelMapping.put( "Iris-setosa", -1.0d );
            labelMapping.put( "Iris-versicolor", 1.0d );
            
            Perceptron p = new Perceptron();
            
            DataProcessor mapping = null;
            
            MapValues labelMapping = new MapValues();
            labelMapping.addMapping( "Iris-setosa", -1.0d );
            labelMapping.addMapping( "Iris-versicolor", 1.0d );
            labelMapping.setDefault( -1.0d );
            
            mapping = labelMapping;
            mapping = new BinaryLabels();
            
            
            
            int i = 0;
            int trainErrors = 0;
            List<Data> dataset = new ArrayList<Data>();
            Data item = stream.readNext();
            while( item != null ){
                
                mapping.process( item );
                dataset.add( item );
                if( i > 0 ){
                    String prediction = p.predict( item ).toString();
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
                String prediction = p.predict( example ).toString();
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
        } catch (Exception e) {
            log.error( "Failed to run test: {}", e.getMessage() );
            e.printStackTrace();
        }
    }
}
