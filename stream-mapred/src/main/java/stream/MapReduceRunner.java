package stream;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.mapred.MapReduce;
import stream.util.CommandLineArgs;

public class MapReduceRunner {

	static Logger log = LoggerFactory.getLogger( MapReduceRunner.class );
	int blockSize;
	int blocks;
	URL train;
	URL test;
	File outputDirectory;



	/**
	 * 
	 * @param params
	 * @throws Exception
	 */
	public static void main(String[] params) throws Exception {
		Properties p = new Properties();

		if( params.length == 1 ){
			File file = new File( params[0] );
			if( !file.canRead() ){
				System.err.println( "Cannot read settings file '" + file.getAbsolutePath() + "'!" );
				System.exit( -1 );
			} else {
				p.load( new FileReader( file ) );
			}
		} else {
			System.err.println( "Usage:" );
			System.err.println( "       java stream.MapReduceRunner properties" );
			System.err.println();
			System.exit( -1 );
		}
		
		p = CommandLineArgs.expandSystemProperties( p );
		CommandLineArgs.populateSystemProperties( p );


		log.info( "" );
		TreeSet<String> opts = new TreeSet<String>();
		for( Object k : p.keySet() )
			opts.add( k.toString() );
		for( String k : opts ){
			String sys = System.getProperty( k );
			if( sys == null )
				sys = p.getProperty(k);
			log.info( "   {} = {}", k, p.getProperty( k ) );
		}
		log.info( "" );
		
		int numberOfMappers = 4;
		try {
			numberOfMappers = Integer.parseInt( System.getProperty( "mapper.threads" ) );
		} catch (Exception e) {
			numberOfMappers = Runtime.getRuntime().availableProcessors();
		}
		log.info( "Using {} concurrent mapper threads", numberOfMappers );
		
		Class<?> mapperClass = Class.forName( System.getProperty( "mapper.class" ) );
		log.info( "Mapper class is {}", mapperClass.getName() );
		Class<?> reducerClass = Class.forName( System.getProperty( "reducer.class" ) );
		log.info( "Reducer class is {}", reducerClass.getName() );

		List<File> inputFiles = new ArrayList<File>();
		File inputDirectory = new File( System.getProperty( "mapper.input" ) );
		if( !inputDirectory.isDirectory() ){
			log.error( "Input-directory '{}' is not a directory!", System.getProperty( "mapper.input" ) );
			return;
		} else {
			
			File[] files = inputDirectory.listFiles();
			if( files != null ){
				for( File f : files ){
					if( f.isFile() ){
						inputFiles.add( f );
					}
				}
			}
		}
		
		File outputFile = new File( System.getProperty( "reducer.output" ) );
		
		//
		// usage is: 
		//
		//     java stream.SgdExperiment -T blockSize -M blocks URL-train URL-test
		//
		//
		File out = outputFile.getParentFile();
		out.mkdirs();
		if( ! out.isDirectory() ){
			log.error( "Failed to create output directory {}", out );
			return;
		}

		log.info( "Processing the following input files:" );
		for( File f : inputFiles ){
			log.info( "   {}", f.getAbsolutePath() );
		}
		log.info( "Writing results to {}", outputFile );
		MapReduce exp = new MapReduce( mapperClass, reducerClass, numberOfMappers, inputFiles, outputFile );
		exp.run();
	}
}