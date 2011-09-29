package stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.hadoop.MapReduce;
import stream.hadoop.SgdMapper;
import stream.hadoop.SgdReducer;
import stream.hadoop.SgdTester;

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
		File dataDir = new File( outputDirectory.getAbsolutePath() + File.separator + "data"  );
		log.info( "Creating {} training data partitions of size {}...", blocks, blockSize );
		List<File> partitions = Partitioner.partition( blockSize, blocks * blockSize, train, dataDir );
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
		out.println( "#T;M;testSetSize;testError" );
		out.print( this.blockSize );
		out.print( ";" + this.blocks );
		out.print( ";" + test.getTestSetSize() );
		out.print( ";" + test.getTestError() );
		out.println();
	}


	/**
	 * @param args
	 */
	public static void main(String[] params) throws Exception {
		
		if( params.length != 5 ){
			System.err.println( "Usage:" );
			System.out.println( "  java stream.SgdExperiment  T  M  training-url  test-url  output-dir" );
			System.out.println();
			System.exit(0);
		}
		
		String[] args = params;
		if( params.length < 1 ){
			args = new String[]{
					"1000",
					"10",
					"http://kirmes.cs.uni-dortmund.de/data/mnist-10k.tt",
					"http://kirmes.cs.uni-dortmund.de/data/mnist-block.svm_light",
					"/tmp"
			};
		}
		
		log.info( "" );
		for( int i = 0; i < args.length; i++ ){
			log.info( "   args[{}] = {}", i, args[i] );
		}
		log.info( "" );
		
		int T = Integer.parseInt( args[0] );
		int M = Integer.parseInt( args[1] );
		log.info( "Using block-size T = {}", T );
		log.info( "Using M = {} number of blocks", M );
		//
		// usage is: 
		//
		//     java stream.SgdExperiment -T blockSize -M blocks URL-train URL-test
		//
		//
		URL train = new URL( args[ 2 ] );
		log.info( "Reading training data from {}", train );

		URL test = new URL( args[ 3 ] );
		log.info( "Reading test data from {}", test );
		
		File out = new File( args[ 4 ] + File.separator + "sgd" + "_T=" + T + "_M=" + M );
		if( args[4].endsWith( File.separator ) )
			 out = new File( args[ 4 ] + "sgd" + "_T=" + T + "_M=" + M );
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