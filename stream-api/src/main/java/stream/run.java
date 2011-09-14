package stream;

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
    }
}