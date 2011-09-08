package stream.io;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;

public class DataStreamLoopTest {

    static Logger log = LoggerFactory.getLogger( DataStreamLoopTest.class );
    
    
    @Test
    public void testReplication() throws Exception {

        List<Data> items = new ArrayList<Data>();
        for( int i = 0; i < 10; i++ ){
            Data item = new DataImpl();
            item.put( "@id", i + "" );
            item.put( "x", Math.random() );
            items.add( item );
        }

        DataStreamLoop loop = new DataStreamLoop();
        loop.setSource( new ListDataStream( items ) );
        loop.setRepeat( 2 );
        loop.setShuffle( true );

        List<Data> result = new ArrayList<Data>();
        Data datum = loop.readNext();
        while( datum != null ){
            result.add( datum );
            log.info( "{}", datum );
            datum = loop.readNext();
        }

        log.info( "loop resulted in {} items", result.size() );
        org.junit.Assert.assertTrue( 30 == result.size() );
    }

}
