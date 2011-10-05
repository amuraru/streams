package stream;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.util.ExperimentLog;

public class Partitioner {
	
	static Logger log = LoggerFactory.getLogger( Partitioner.class );
	public final static String PROPERTY_BLOCK_SIZE = "block.size";
	public final static String PROPERTY_LIMIT = "limit";
	public final static String PROPERTY_OUTPUT = "output";
	
	Random rnd = new Random();
	
	
	protected void initOuputDirectory( File outputDirectory ) throws Exception {

		if( outputDirectory.isFile() )
			throw new Exception( "Output directory '" + outputDirectory.getAbsolutePath() + "' is a file!" );
		
		if( ! outputDirectory.isDirectory() )
			outputDirectory.mkdirs();
		
		if( !outputDirectory.isDirectory() )
			throw new Exception( "Failed to create outputDirectory '" + outputDirectory.getAbsolutePath() + "'!" );
		
	}
	
	public List<File> shuffledPartitions( int blockSize, int numberOfPartitions, int limit, URL url, File outputDirectory ) throws Exception {
		List<File> partitions = new ArrayList<File>( numberOfPartitions );
		List<BlockWriter> writers = new ArrayList<BlockWriter>( numberOfPartitions );
		File f = new File(url.getFile());
		String name = f.getName();
		
		initOuputDirectory( outputDirectory );
		
		long seed = System.currentTimeMillis();
		try {
			seed = Long.parseLong( System.getProperty( "global.random.seed" ) );
		} catch (Exception e) {
			seed = System.currentTimeMillis();
		}
		PrintStream stats = new PrintStream( new FileOutputStream( outputDirectory.getAbsolutePath() + File.separator + "shuffling.txt" ) );
		stats.println( "Using random seed " + seed );
		stats.close();
		rnd = new Random( seed );

		DecimalFormat fmt = new DecimalFormat( "0000" );

		for( int i = 0; i< numberOfPartitions; i++ ){
			File partFile = new File( outputDirectory.getAbsolutePath() + File.separator + name + ".part" + fmt.format( i ) );
			BlockWriter writer = new BlockWriter( partFile );
			partitions.add( partFile );
			writers.add( writer );
		}

		Long total = new Long( blockSize * numberOfPartitions );
		fmt = new DecimalFormat( "0.00%" );
		
		log.debug( "Created {} file-writers", writers.size() );
		Long count = 0L;
		BufferedReader r = new BufferedReader( new InputStreamReader( url.openStream() ) );
		String line = r.readLine();
		while( line != null && !writers.isEmpty() && count < limit ){
			
			int idx = rnd.nextInt( writers.size() );
			BlockWriter w = writers.get( idx );
			w.add( line );
			
			if( w.size() >= blockSize ){
				log.info( "Block writer {} is full, closing file {} and removing the writer", idx, w.getFile() );
				w.close();
				writers.remove( w );
			}
			
			if( writers.isEmpty() ){
				log.debug( "No more writers left (all blocks full)" );
			}
			
			count++;
			
			if( count % 100000 == 0 ){
				log.info( "Processed {} examples,  {} completed", count, fmt.format( 100.0 * count.doubleValue() / total.doubleValue() ) );
				ExperimentLog.log( "Processed {} examples,  {} completed", count, fmt.format( 100.0 * count.doubleValue() / total.doubleValue() ) );
			}
			
			line = r.readLine();
		}
		
		r.close();

		// take care of the left-overs
		//
		if( ! writers.isEmpty() ){
			log.warn( "Not all buckets have been fully filled with data, closing remaining {} blocks", writers.size() );
			for( FileWriter writer : writers ) {
				writer.close();
			}
		}
		
		return partitions;
	}
	
	

	public List<File> createPartitions( int blockSize, int numberOfPartitions, int limit, URL url, File outputDirectory ) throws Exception {
		List<File> partitions = new ArrayList<File>( numberOfPartitions );
		List<BlockWriter> writers = new ArrayList<BlockWriter>( numberOfPartitions );
		File f = new File(url.getFile());
		String name = f.getName();

		DecimalFormat fmt = new DecimalFormat( "0000" );

		for( int i = 0; i< numberOfPartitions; i++ ){
			File partFile = new File( outputDirectory.getAbsolutePath() + File.separator + name + ".part" + fmt.format( i ) );
			BlockWriter writer = new BlockWriter( partFile );
			partitions.add( partFile );
			writers.add( writer );
		}

		log.debug( "Created {} file-writers", writers.size() );
		
		if( outputDirectory.isFile() )
			throw new Exception( "Output directory '" + outputDirectory.getAbsolutePath() + "' is a file!" );
		
		if( ! outputDirectory.isDirectory() )
			outputDirectory.mkdirs();
		
		if( !outputDirectory.isDirectory() )
			throw new Exception( "Failed to create outputDirectory '" + outputDirectory.getAbsolutePath() + "'!" );
		
		int count = 0;
		BufferedReader r = new BufferedReader( new InputStreamReader( url.openStream() ) );
		String line = r.readLine();
		while( line != null && !writers.isEmpty() && count < limit ){
			
			int idx = 0;
			BlockWriter w = writers.get( idx );
			w.add( line );
			
			if( w.size() >= blockSize ){
				log.debug( "Block writer {} is full, closing file {} and removing the writer", idx, w.getFile() );
				w.close();
				writers.remove( w );
			}
			
			if( writers.isEmpty() ){
				log.debug( "No more writers left (all blocks full)" );
			}
			
			count++;
			line = r.readLine();
		}
		
		r.close();

		// take care of the left-overs
		//
		if( ! writers.isEmpty() ){
			log.warn( "Not all buckets have been fully filled with data, closing remaining {} blocks", writers.size() );
			for( FileWriter writer : writers ) {
				writer.close();
			}
		}
		
		return partitions;
	}
	
	
	public class BlockWriter extends FileWriter {

		File file;
		int size = 0;
		
		/**
		 * @param file
		 * @throws IOException
		 */
		public BlockWriter(File file) throws IOException {
			super(file);
			this.file = file;
		}
		
		public void add( String line ) throws IOException {
			append( line + "\n" );
			size++;
		}
		
		public File getFile(){
			return file;
		}

		public int size(){
			return size;
		}
	}
}
