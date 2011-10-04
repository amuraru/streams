

import java.io.File;
import java.net.URL;

import stream.util.CommandLineArgs;

public class Partitioner {

	/**
	 * @param args
	 */
	public static void main(String[] params) {
		String[] args = params;
		URL url;
		
		
		if( args.length < 1 ){
			System.out.println( "Usage:" );
			System.out.println( "    java ... stream.hadoop.Partitioner [-b LINES] [-l LIMIT] URL" );
			System.out.println();
			return;
		}

		CommandLineArgs cla = new CommandLineArgs( params );
		cla.dumpArgs();
		cla.setSystemProperties( "partitioner" );
		
		if( cla.getArguments().size() > 0 )
			System.setProperty( "partitioner.input.url", cla.getArguments().get( 0 ) );
		
		if( cla.getArguments().size() > 1 )
			System.setProperty( "partitioner.output", cla.getArguments().get( 1 ) );
		
		try {
			
			int limit = Integer.parseInt( cla.getOption( "limit", "" + Integer.MAX_VALUE ) );
			int lines = Integer.parseInt( cla.getOption( "block.size", "1000" ) );
			int parts = Integer.parseInt( cla.getOption( "max.parts", "10" ) );
			
			System.out.println( "Using block-size of " + lines + " lines" );
			System.out.println( "Creating blocks from a maximum of " + limit + " examples" );
			
			
			url = new URL( cla.getArguments().get( 0 ) );
			File outputDirectory = new File( "." );
			
			if( cla.getArguments().size() > 1 )
				outputDirectory = new File( cla.getArguments().get( 1 ) );
			
			stream.Partitioner p = new stream.Partitioner();
			if( cla.getOption( "shuffle" ) != null )
				p.shuffledPartitions(lines, parts, limit, url, outputDirectory);
			else
				p.createPartitions( lines, parts, limit, url, outputDirectory );

			
		} catch (Exception e) {
			System.out.println( "Error: " + e.getMessage() );
		}
	}
}