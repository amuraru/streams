package stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.stats.Statistics;
import stream.data.stats.StatisticsPublisher;
import stream.mapred.MapReduce;
import stream.optimization.SgdMapper;
import stream.optimization.SgdReducer;
import stream.optimization.SgdTester;
import stream.util.CommandLineArgs;
import stream.util.ParameterDiscovery;

public class SgdExperiment {

	static Logger log = LoggerFactory.getLogger( SgdExperiment.class );
	int blockSize;
	int blocks;
	URL train;
	URL test;
	File outputDirectory;

	public SgdExperiment( int blockSize, int blocks, URL train, URL test, File outDir ){
		this.blockSize = blockSize;
		this.blocks = blocks;
		this.train = train;
		this.test = test;
		this.outputDirectory = outDir;
	}


	public List<File> prepareBlocks() throws Exception {

		int limit = blocks * blockSize;
		try {
			limit = Integer.parseInt( System.getProperty( "data.limit" ) );
		} catch (Exception e) {
			limit = blocks * blockSize;
		}

		if( "file".equalsIgnoreCase( train.toURI().getScheme() ) ){
			log.info( "Training source is a file URL" );
			File file = new File( train.toURI() );
			if( file.isDirectory() ){
				log.info( "Training URL denotes a directory, will use all files in that directory as blocks!" );
				File[] files = file.listFiles();
				if( files != null ){

					List<File> blockFiles = new ArrayList<File>();
					for( File f : files ){
						if( f.isFile() ){
							blockFiles.add( f );
						}
					}
					return blockFiles;
				}
			}
		}

		log.info( "Creating {} partitions of at most {} data points", blocks, limit );
		File dataDir = new File( outputDirectory.getAbsolutePath() + File.separator + "data"  );
		log.info( "Training data partitions will be of size {}...", blockSize );
		Partitioner p = new Partitioner();
		List<File> partitions = p.shuffledPartitions( blockSize, blocks, limit, train, dataDir );
		//p.createPartitions( blockSize, blocks, limit, train, dataDir );
		log.info( "Blocks created in {}", dataDir );
		return partitions;
	}


	public void run() throws Exception {
		//
		//
		File modelFile = new File( outputDirectory.getAbsolutePath() + File.separator + "model.out" );

		log.info( "Preparing the training data..." );
		List<File> blocks = prepareBlocks();

		log.info( "Running SGD on {} blocks", blocks.size() );
		log.info( "Starting Map&Reduce with {} as mapper and {} as reducer", SgdMapper.class, SgdReducer.class );
		log.info( "   using {} mappers", this.blocks );
		log.info( "" );
		log.info( " mapper args:   {}", ParameterDiscovery.getSystemProperties( "mapper.args" ) );
		log.info( " reducer args:  {}", ParameterDiscovery.getSystemProperties( "reducer.args" ) );
		
		MapReduce mapReduce = new MapReduce( SgdMapper.class, SgdReducer.class, this.blocks, blocks, modelFile );
		File weights = mapReduce.run();

		File dataDir = null;
		for( File block : blocks ){
			dataDir = block.getParentFile();
			block.delete();
		}
		if( dataDir != null )
			dataDir.delete();

		log.info( "Map&Reduce finished." );
		log.info( "" );
		log.info( "Starting test phase..." );


		SgdTester test = SgdTester.computeTestError( weights, this.test );
		log.info( "" );
		log.info( "Error on test set is: {}", test.getTestError() );
		log.info( "" );

		File resultFile = new File( outputDirectory.getAbsolutePath() + File.separator + "test.error" );
		PrintStream out = new PrintStream( new FileOutputStream( resultFile ) );
		
		Statistics stats = new Statistics();
		stats.add( "T", new Double( blockSize ) );
		stats.add( "M", new Double( blocks.size() ) );
		stats.add( "testSize", test.getTestSetSize() );
		stats.add( "testError", test.getTestError() );
		stats.add( "mapTime", mapReduce.getMapTime().doubleValue() );
		stats.add( "reduceTime", mapReduce.getReduceTime().doubleValue() );
		
		
		String resultUrl = System.getProperty( "experiment.result.url" );
		if( resultUrl != null ){
			log.info( "Sending results to {}", resultUrl );
			try {
				URL destination = new URL( resultUrl );
				StatisticsPublisher.publish( destination, stats );
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.warn( "No result URL defined in property 'experiment.result.url'" );
		}
		
		out.println( "#T;M;testSetSize;testError" );
		out.print( this.blockSize );
		out.print( ";" + this.blocks );
		out.print( ";" + test.getTestSetSize() );
		out.print( ";" + test.getTestError() );
		out.println();
		
		
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
			System.err.println( "       java stream.SgdExperiment experiment.properties" );
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

		int T = Integer.parseInt( System.getProperty( "experiment.T" ) );
		int M = Integer.parseInt( System.getProperty( "experiment.M" ) );
		URL train = new URL( System.getProperty( "experiment.training.url" ) );
		URL test = new URL( System.getProperty( "experiment.test.url" ) );
		String outDir = "" + System.getProperty( "experiment.output" );

		log.info( "Using block-size T = {}", T );
		log.info( "Using M = {} number of blocks", M );
		//
		// usage is: 
		//
		//     java stream.SgdExperiment -T blockSize -M blocks URL-train URL-test
		//
		//
		log.info( "Reading training data from {}", train );
		log.info( "Reading test data from {}", test );

		File out = new File( outDir );
		out.mkdirs();
		if( ! out.isDirectory() ){
			log.error( "Failed to create output directory {}", out );
			return;
		}

		log.info( "Writing results to {}", out );

		SgdExperiment exp = new SgdExperiment( T, M, train, test, out );
		exp.run();
	}
}