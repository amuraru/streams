package stream.hadoop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Map {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		if( args.length < 3 ){
			System.out.println( "Usage:" );
			System.out.println( "   java stream.hadoop.Map map.class.name file1 file2 file2" );
		}
		
		
		String className = args[0];
		System.out.println( "Mapper: " + className );
		Class<?> clazz = Class.forName( className );

		List<Thread> mappers = new ArrayList<Thread>();
		
		for( int i = 2; i < args.length; i++ ){
			final File file = new File( args[i] );
			final File outFile = new File( args[i] + ".map-output" );
			System.out.println( "Creating mapper for " + file.getAbsolutePath() );
			
			final InputStream in = new FileInputStream( file );
			final OutputStream out = new FileOutputStream( outFile );
			
			final AbstractStreamMapper map = (AbstractStreamMapper) clazz.newInstance();
			Thread t = new Thread( new Runnable(){
				@Override
				public void run() {
					System.out.println( "Mapper " + file.getAbsolutePath() + " ~> " + outFile.getAbsolutePath() + " is starting..." );
					map.run( in, out );
					System.out.println( "Mapper " + file.getAbsolutePath() + " ~> " + outFile.getAbsolutePath() + " is finished" );
				}
			});
			mappers.add( t );
			t.start();
		}
		
		while( ! mappers.isEmpty() ){
			Thread t = mappers.get( 0 );
			System.out.println( "Waiting for mapper " + t + " to finish... " + mappers.size() + " mappers running." );
			t.join();
			mappers.remove( 0 );
		}
		
		System.out.println( "All mappers finished." );
	}
}
