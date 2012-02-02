package stream;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.tools.StreamRunner;

public class StreamRunnerTest
{
    static Logger log = LoggerFactory.getLogger( StreamRunnerTest.class );

	// a test dummy
	@Test
	public void test(){
	}

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        URL url = StreamRunner.class.getResource( "/example.xml" );
        log.info( "Running experiment from {}", url );
        StreamRunner runner = new StreamRunner( url );
        runner.run();
    }
}