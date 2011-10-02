package stream.mapred;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.CommandLineArgs;
import stream.util.ExperimentLog;
import stream.util.ParameterInjection;

public class MapReduce {

	static Logger log = LoggerFactory.getLogger( MapReduce.class );

	static String[] PREFIXES = new String[]{ 
		"", "stream.", "stream.mapred.", "stream.mapper.", "stream.reducer.", "stream.hadoop.", "stream.optimization." 
	};

	String experimentName = "";
	Class<?> mapperClass;
	Class<?> reducerClass;
	int numberOfMappers = 4;
	List<File> dataFiles = new ArrayList<File>();
	File outputFile;
	long mapTime = 0L;
	long reduceTime = 0L;

	public MapReduce( Class<?> mapperClass, Class<?> reducerClass, int mappers, List<File> inputBlocks, File outputFile ){
		experimentName = ExperimentLog.getExperimentName();
		ExperimentLog.log( "================================================" );
		ExperimentLog.log( experimentName, "Starting Map&Reduce Experiment '{}'", experimentName );
		ExperimentLog.log( "Initializing Map&Reduce process" );
		this.mapperClass = mapperClass;
		this.reducerClass = reducerClass;
		this.numberOfMappers = mappers;
		this.dataFiles.addAll( inputBlocks );
		this.outputFile = outputFile;
		ExperimentLog.log( "    Mapper:  {}", mapperClass );
		ExperimentLog.log( "    Reducer: {}", reducerClass );
		
		ExperimentLog.log( "  Blocks to process:" );
		for( File file : inputBlocks ){
			ExperimentLog.log( ExperimentLog.getExperimentName(), "    file: {}", file.getAbsolutePath() );
		}
	}


	public StreamReducer createReducer() throws Exception {
		StreamReducer reducer = (StreamReducer) reducerClass.newInstance();
		ParameterInjection.injectSystemProperties( reducer, "reducer.args" );
		return reducer;
	}


	public StreamMapper createMapper() throws Exception {
		StreamMapper mapper = (StreamMapper) mapperClass.newInstance();
		ParameterInjection.injectSystemProperties( mapper, "mapper.args" );
		return mapper;
	}



	public List<File> doMap( List<File> data ) throws Exception {
		List<File> inputs = new ArrayList<File>( data );
		List<File> outputs = new ArrayList<File>();
		List<Thread> mappers = new ArrayList<Thread>();

		log.info( "#" );
		log.info( "#  >>> Starting MAP phase..." );
		ExperimentLog.log( "Starting MAP phase, {} block(s) need to be processed", data.size() );
		try {
			numberOfMappers = Integer.parseInt( System.getProperty( "mapper.threads" ) );
			log.info( "#  Using a maximum of {} parallel mapper-threads", numberOfMappers );
		} catch (Exception e){
			numberOfMappers = dataFiles.size();
		}
		log.info( "#" );
		long start = System.currentTimeMillis();

		while( ! mappers.isEmpty() || ! inputs.isEmpty() ){

			Iterator<Thread> it = mappers.iterator();
			while( it.hasNext() ){
				Thread t = it.next();
				if( ! t.isAlive() ){
					log.debug( "#  Mapper {} finished, removing from list", t );
					it.remove();
				}
			}

			while( mappers.size() < this.numberOfMappers && !inputs.isEmpty() ){
				File input = inputs.remove( 0 );
				log.debug( "#   Creating mapper for {}", input );
				File outputFile = createOutfile( input );
				outputs.add( outputFile );
				Thread t = createMapper( mapperClass, input, outputFile);
				mappers.add( t );
				t.start();
			}
			try {
				if( mappers.size() + inputs.size() > 0 ){
					log.info( "#    {} mappers running, {} input-files waiting to be processed", mappers.size(), inputs.size() );
					Thread.sleep( 1000 );
				}
			} catch (Exception e) {
			}
		}
		mapTime = System.currentTimeMillis() - start;
		ExperimentLog.log( "Map phase completed in {} ms", mapTime );
		log.info( "#  All mappers finished." );
		log.info( "# ");
		log.info( "#  >>> MAP phase complete." );
		log.info( "# ");
		log.info( "# Map phase required {} ms", mapTime );
		return outputs;
	}



	public void doReduce( List<File> outputs, File finalOutput ) throws Exception {
		log.info( "###########################################################################################" );
		log.info( "#" );
		File tmp = File.createTempFile( "map_reduce_tmp", "__" );
		log.info( "#   Creating intermediate output");
		log.info( "#   Temporary output is created in '{}'", tmp.getAbsolutePath() );
		ExperimentLog.log( "Collecting intermediate outputs" );
		tmp.deleteOnExit();
		FileWriter intermediate = new FileWriter( tmp );
		for( File mapped : outputs ){

			char[] buf = new char[ 4096 ];
			FileReader reader = new FileReader( mapped );
			int read = reader.read( buf );
			int total = read;
			while( read > 0 ){
				intermediate.write( buf, 0, read );
				read = reader.read( buf );
				if( read > 0 )
					total += read;
			}
			log.info( "#   " + total + " bytes appended to " + tmp.getAbsolutePath() );
			reader.close();
			mapped.delete();
		}
		intermediate.close();

		long startReduce = System.currentTimeMillis();
		log.info( "#" );
		log.info( "###########################################################################################" );
		log.info( "#" );
		log.info( "#  >>> Starting REDUCE phase..." );
		ExperimentLog.log( experimentName, "Starting REDUCE phase" );
		log.info( "#" );
		FileInputStream mapInput = new FileInputStream( tmp );
		StreamReducer reducer = createReducer();

		log.info( "#  - Starting reducer, output is: " + finalOutput.getAbsolutePath() );
		FileOutputStream fos = new FileOutputStream( finalOutput );
		reducer.reduce( mapInput, fos );
		fos.close();
		reduceTime = System.currentTimeMillis() - startReduce;
		ExperimentLog.log( "REDUCE completed in {} ms", reduceTime );
		log.info( "#  - REDUCE finished." );
		log.info( "#  - Output is in file {}", finalOutput.getAbsolutePath() );
		log.info( "#" );
		log.info( "#  Reducing required {} ms", reduceTime );
		log.info( "#" );
		log.info( "###########################################################################################" );
	}



	public File run() throws Exception {

		//ExperimentLog.log( experimentName, "========================================================" );

		if( dataFiles.isEmpty() ){
			log.info( "No data files provided, running single-threaded map-reduce on standard input" );
			File intermediate = File.createTempFile( "_intermediate_result", ".dat" );
			//intermediate.deleteOnExit();
			log.debug( "Writing intermediate results to " + intermediate.getAbsolutePath() );

			OutputStream out = new FileOutputStream( intermediate );

			log.info( "Running Mapper on standard input..." );
			StreamMapper mapper = createMapper();
			mapper.run( System.in, out);
			out.flush();
			out.close();

			OutputStream result = System.out;
			if( outputFile != null ){
				log.info( "Writing output to " + outputFile.getAbsolutePath() );
				result = new FileOutputStream( outputFile );
			} else {
				log.info( "Writing output to standard output" );
			}

			log.info( "Running Reducer on intermediate results..." );
			FileInputStream in = new FileInputStream( intermediate );
			StreamReducer reducer = createReducer();
			reducer.reduce( in, result );
			result.flush();
			result.close();

			ExperimentLog.log( "Map-Phase required:      {} ms", mapTime );
			ExperimentLog.log( "Reduce-phase required:   {} ms", reduceTime );
			ExperimentLog.log( "------------------------------------------" );
			ExperimentLog.log( "Total processing time is {} ms", reduceTime + mapTime );
			ExperimentLog.log( "========================================================" );

			return outputFile;
		}


		log.info( "#  Running 'map' on {} blocks using {} parallel mappers", dataFiles.size(), numberOfMappers );
		List<File> mappedBlocks = doMap( dataFiles );

		log.info( "#  Running 'reduce' on {} results", mappedBlocks.size() );
		doReduce( mappedBlocks, outputFile );

		log.info( "#  Ouput is written to {}", outputFile );
		log.info( "#" );
		log.info( "###########################################################################################" );
		log.info( "# ");
		ExperimentLog.log( "Map-Phase required:      {} ms", mapTime );
		ExperimentLog.log( "Reduce-phase required:   {} ms", reduceTime );
		ExperimentLog.log( "------------------------------------------" );
		ExperimentLog.log( "Total processing time is {} ms", reduceTime + mapTime );
		ExperimentLog.log( "========================================================" );
		log.info( "#  Map-Phase required:      {} ms", mapTime );
		log.info( "#  Reduce-phase required:   {} ms", reduceTime );
		log.info( "#  ------------------------------------------" );
		log.info( "#  Total processing time is {} ms", reduceTime + mapTime );
		log.info( "# ");
		log.info( "###########################################################################################" );
		return outputFile;
	}


	public static Class<?> findClass( String className ){
		for( String prefix : PREFIXES ){
			try {
				Class<?> clazz = Class.forName( prefix + className );
				if( clazz != null )
					return clazz;
			} catch (Exception e) {
			}
		}
		return null;
	}


	public static File createOutfile( File inputFile ){
		return new File( inputFile.getAbsolutePath() + ".map-output" );
	}

	public Thread createMapper( Class<?> mapClass, File inputFile, final File outFile ) throws Exception {
		final File file = inputFile;
		log.debug( "#  - Creating mapper for " + file.getAbsolutePath() );

		final InputStream in = new FileInputStream( file );
		final OutputStream out = new FileOutputStream( outFile );

		final StreamMapper map = createMapper();

		Thread t = new Thread( new Runnable(){
			@Override
			public void run() {
				log.debug( "#  - Mapper " + file.getName() + " ~> " + outFile.getName() + " is starting..." );
				try {
					map.run( in, out );
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.debug( "#  - Mapper " + file.getName() + " ~> " + outFile.getName() + " is finished" );
			}
		});
		return t;
	}


	public Long getMapTime(){
		return mapTime;
	}

	public Long getReduceTime(){
		return reduceTime;
	}




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
			System.err.println( "       java MapReduce properties" );
			System.err.println();
			System.exit( -1 );
		}

		p = CommandLineArgs.expandSystemProperties( p );
		CommandLineArgs.populateSystemProperties( p );


		log.debug( "" );
		TreeSet<String> opts = new TreeSet<String>();
		for( Object k : p.keySet() ){
			opts.add( k.toString() );
		}
		for( String k : opts ){
			String sys = System.getProperty( k );
			if( sys == null )
				sys = p.getProperty(k);
			log.debug( "   " + k + " = " + p.getProperty( k ) );
		}
		log.debug( "" );

		int numberOfMappers = 4;
		try {
			numberOfMappers = Integer.parseInt( System.getProperty( "mapper.threads" ) );
		} catch (Exception e) {
			numberOfMappers = Runtime.getRuntime().availableProcessors();
		}
		log.debug( "Using a maximum of " + numberOfMappers + " concurrent mapper threads" );

		log.info( "" );
		Class<?> mapperClass = findClass( System.getProperty( "mapper.class" ), new String[]{ "", "Mapper" }, PREFIXES );
		log.info( "  Mapper class is " + mapperClass.getName() );
		Class<?> reducerClass = findClass( System.getProperty( "reducer.class" ), new String[]{ "", "Reducer" }, PREFIXES );
		log.info( "  Reducer class is " + reducerClass.getName() );
		log.info( "" );

		List<File> inputFiles = new ArrayList<File>();
		if( System.getProperty( "mapper.input" ) != null ){
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
		}

		File outputFile = null;
		if( System.getProperty( "reducer.output" ) != null )
			outputFile = new File( System.getProperty( "reducer.output" ) );

		//
		// usage is: 
		//
		//     java stream.SgdExperiment -T blockSize -M blocks URL-train URL-test
		//
		//
		if( outputFile != null ){
			File out = outputFile.getParentFile();
			out.mkdirs();
			if( ! out.isDirectory() ){
				log.error( "Failed to create output directory {}", out );
				return;
			}
		}
		if( !inputFiles.isEmpty() ){
			log.info( "Processing the following {} input files:", inputFiles.size() );
			for( File f : inputFiles ){
				log.info( "   {}", f.getAbsolutePath() );
			}
		}
		if( outputFile != null )
			log.info( "Writing results to {}", outputFile );
		stream.mapred.MapReduce exp = new stream.mapred.MapReduce( mapperClass, reducerClass, numberOfMappers, inputFiles, outputFile );
		exp.run();
	}

	
	
	public static Class<?> findClass( String name, String[] suffixes, String[] paths ){
		Class<?> clazz;
		try {
			clazz = Class.forName( name );
			if( clazz != null )
				return clazz;
		} catch (Exception e){
		}

		for( String path : paths ){
			for( String suffix : suffixes ){

				try {
					clazz = Class.forName( path + name + suffix );
					if( clazz != null ){
						log.debug( "Found class {}", clazz );
						return clazz;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}
}