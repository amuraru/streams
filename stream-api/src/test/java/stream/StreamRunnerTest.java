package stream;

import java.net.URL;

import stream.tools.StreamRunner;

public class StreamRunnerTest
{
    /**
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        URL url = StreamRunner.class.getResource( "/example.xml" );
        StreamRunner runner = new StreamRunner( url );
        runner.run();
    }
}