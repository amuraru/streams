/**
 * 
 */
package stream.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chris
 *
 */
public class GnuplotRunner
	extends Thread
{
	static Logger log = LoggerFactory.getLogger( GnuplotRunner.class );
	public static String GNUPLOT = "/usr/bin/gnuplot";
	
	static String[] paths = new String[]{
		"/bin", "/usr/bin", "/usr/local/bin", "/sw/bin"
	};
	
	
	File directory;
	boolean running = true;
	long delay;
	
	public GnuplotRunner( File dir, long delay ){
		this.directory = dir;
		this.delay = delay;
		this.setDaemon( true );
	}
	
	
	public static String findGnuplot(){
		for( String path : paths ){
			File file = new File( path + File.separator + "gnuplot" );
			if( file.exists() && file.canExecute() ){
				log.debug( "Found gnuplot at {}", file.getAbsolutePath() );
				return file.getAbsolutePath();
			}
		}
		
		return GNUPLOT;
	}
	
	
	public static void createPlots( File dir ) throws Exception {
	    
	    File lock = new File( dir.getAbsolutePath() + File.separator + ".lock_gnuplot" );
	    if( lock.exists() ){
	        return;
	    }
	    
	    lock.createNewFile();
	    
		String cmd = createPlotCommand( "plot.cmd", dir );
		String gnuplot = findGnuplot();
		Runtime.getRuntime().exec( gnuplot + " " + cmd, new String[0], dir );
		
		lock.delete();
	}

	public static String createPlotCommand( String name, File dir ) throws Exception {
		File plotCmd = new File( dir.getAbsolutePath() + File.separator + name );
	
		PrintStream out = new PrintStream( new FileOutputStream( plotCmd ) );
		out.println( "set terminal png size 1000,400" );
		out.println( "set grid" );

		for( File f : dir.listFiles() ){
			if( f.getName().endsWith( ".dat" ) ){
				String header = firstLine( f );
				if( header != null ){
					out.println( "set output \"" + f.getName().replaceAll( "dat$", "png" ) + "\"");
					out.print( "plot " );
					String[] cols = header.split( " " );
					for( int i = 1; i < cols.length; i++ ){
						out.print( "\"" + f.getName() + "\" using 1:" + (i+1) + " with lines title '" + cols[i] + "'" );
						if( i + 1 < cols.length ){
							out.print( ", " );
						}
					}
					out.println();
					out.println();
				}
			}
		}
		
		return plotCmd.getAbsolutePath();
	}

	public static String firstLine( File file ) throws Exception {
		try {
			BufferedReader r = new BufferedReader( new FileReader( file ) );
			String line = r.readLine();
			r.close();
			return line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while( running ){
			try {
				log.debug( "Sleeping {} ms", delay );
				Thread.sleep( delay );
				log.debug( "Running gnuplot..." );
				GnuplotRunner.createPlots( this.directory );
			} catch (InterruptedException ie ){
				if( running )
					ie.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			log.info( "Creating final plots..." );
			GnuplotRunner.createPlots( this.directory );
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void shutdown(){
		running = false;
		interrupt();
	}


	public static void main( String[] args ) throws Exception {
		File dir = new File( "/Users/chris/Uni/Projekte/stream-mining/output" );
		GnuplotRunner.createPlots( dir );
	}
}