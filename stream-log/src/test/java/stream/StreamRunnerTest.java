package stream;

import java.net.URL;

import org.junit.Test;

import stream.tools.StreamRunner;

public class StreamRunnerTest
{

	// a test dummy
	@Test
	public void test(){
	}

    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        URL url = StreamRunner.class.getResource( "/demo-shop.xml" );
        StreamRunner runner = new StreamRunner( url );
        runner.run();
    }
}