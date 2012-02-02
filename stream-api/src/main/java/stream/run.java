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

        try {
            
            URL url = null;
            
            File f = new File( args[0] );
            if( f.isFile() ){
                url = new URL( "file:" + f.getAbsolutePath() );
            } else {
                url = new URL( args[0] );
            }

            StreamRunner runner = new StreamRunner( url );
            runner.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}