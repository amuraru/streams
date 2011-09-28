package stream;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Partitioner {

	public static List<File> partition( int blockSize, int limit, URL url, File outputDirectory ) throws Exception {
		List<File> partitions = new ArrayList<File>();
		File f = new File(url.getFile());
		String name = f.getName();
		
		if( outputDirectory.isFile() )
			throw new Exception( "Output directory '" + outputDirectory.getAbsolutePath() + "' is a file!" );
		
		if( ! outputDirectory.isDirectory() )
			outputDirectory.mkdirs();
		
		if( !outputDirectory.isDirectory() )
			throw new Exception( "Failed to create outputDirectory '" + outputDirectory.getAbsolutePath() + "'!" );
		
		int part = 0;
		DecimalFormat fmt = new DecimalFormat( "0000" );
		
		int count = 0;
		PrintStream out = null;
		BufferedReader r = new BufferedReader( new InputStreamReader( url.openStream() ) );
		String line = r.readLine();
		while( line != null && count < limit ){
			if( out == null || count % blockSize == 0 ){
				if( out != null ){
					out.close();
				}
				File file = new File( outputDirectory.getAbsolutePath() + File.separator + name + ".part" + fmt.format( part ) );
				partitions.add( file );
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
		return partitions;
	}
}
