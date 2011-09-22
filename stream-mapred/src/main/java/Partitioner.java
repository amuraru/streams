

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.text.DecimalFormat;

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
		
		try {
			int limit = Integer.MAX_VALUE;
			int lines = 1000;
			
			int i = 0;
			
			if( "-b".equals( args[i] ) || "--block-size".equals( args[i] ) ){
				System.out.println( "Adding block-size " + args[i] + " " + args[i+1] );
				lines = Integer.parseInt( args[i+1] );
				i++;
				i++;
			}
			
			if( "-l".equals( args[i] ) || "--limit".equals( args[i] ) ){
				System.out.println( "Adding limit " + args[i] + " " + args[i+1] );
				limit = Integer.parseInt( args[i+1] );
				i++;
				i++;
			}
			
			System.out.println( "Using block-size of " + lines + " lines" );
			System.out.println( "Creating blocks from a maximum of " + limit + " examples" );
			
			url = new URL( args[i] );
			File f = new File(url.getFile());
			String name = f.getName();
			
			int part = 0;
			DecimalFormat fmt = new DecimalFormat( "0000" );
			
			int count = 0;
			PrintStream out = null;
			BufferedReader r = new BufferedReader( new InputStreamReader( url.openStream() ) );
			String line = r.readLine();
			while( line != null && count < limit ){
				if( out == null || count % lines == 0 ){
					if( out != null )
						out.close();
					File file = new File( name + ".part" + fmt.format( part ) );
					out = new PrintStream( new FileOutputStream( file ) );
					part++;
					System.out.println( "Writing to file " + file.getAbsolutePath() );
				}
				count++;
				out.println( line );
				line = r.readLine();
			}
			
			if( out != null )
				out.close();
			
			r.close();
			
		} catch (Exception e) {
			System.out.println( "Error: " + e.getMessage() );
		}
	}
}
