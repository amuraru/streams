package stream;

import java.io.File;
import java.net.URL;

import stream.tools.StreamRunner;

public class run
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if( args.length == 0 ){
            System.out.println();
            System.out.println("Usage:\n");
            System.out.println("    java -cp stream.jar stream.run /path/to/stream-experiment.xml" );
            System.out.println();
            System.exit( -1 );
        }
        
        URL url = null;
        
        
        File file = new File( args[0] );
        if( file.isFile() ){
        	try {
        		url = file.toURI().toURL();
        	} catch (Exception e) {
        	}
        }
        
        try {
        	if( url == null )
        		url = new URL( args[0] );

        	System.out.println( "Reading experiment layout from " + url );
        	StreamRunner runner = new StreamRunner( url );
        	System.out.println( "Starting stream experiment..." );
        	runner.run();
        	
        } catch (Exception e) {
        	System.err.println( e.getMessage() );
        }
    }
}