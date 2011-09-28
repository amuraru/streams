package stream.hadoop;


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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapReduce {

    static Logger log = LoggerFactory.getLogger( MapReduce.class );

    static String[] PREFIXES = new String[]{ 
        "", "stream.", "stream.mapred.", "stream.map.", "stream.reduce.", "stream.hadoop.", "stream.optimization." 
    };

    Class<?> mapperClass;
    Class<?> reducerClass;
    int numberOfMappers = 4;
    List<File> dataFiles = new ArrayList<File>();
    File outputFile;
    long mapTime = 0L;
    long reduceTime = 0L;

    public MapReduce( Class<?> mapperClass, Class<?> reducerClass, int mappers, List<File> inputBlocks, File outputFile ){
    	this.mapperClass = mapperClass;
    	this.reducerClass = reducerClass;
    	this.numberOfMappers = mappers;
    	this.dataFiles.addAll( inputBlocks );
    	this.outputFile = outputFile;
    }
    
    
    
    public List<File> doMap( List<File> data ) throws Exception {
    	List<File> inputs = new ArrayList<File>( data );
    	List<File> outputs = new ArrayList<File>();
    	List<Thread> mappers = new ArrayList<Thread>();
    	
        log.info( "#" );
        log.info( "#  >>> Starting MAP phase..." );
        try {
        	numberOfMappers = Integer.parseInt( System.getProperty( "maxThreads" ) );
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
        log.info( "#  All mappers finished." );
        log.info( "# ");
        log.info( "#  >>> MAP phase complete." );
        log.info( "# ");
        log.info( "# Map phase required {} ms", mapTime );
        return outputs;
    }
    
    
    
    @SuppressWarnings("rawtypes")
	public void doReduce( List<File> outputs, File finalOutput ) throws Exception {
        log.info( "###########################################################################################" );
        log.info( "#" );
        File tmp = File.createTempFile( "map_reduce_tmp", "__" );
        log.info( "#   Creating intermediate output");
        log.info( "#   Temporary output is created in '{}'", tmp.getAbsolutePath() );
        //tmp.deleteOnExit();
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
            //mapped.delete();
        }
        intermediate.close();
        
        long startReduce = System.currentTimeMillis();
        log.info( "#" );
        log.info( "###########################################################################################" );
        log.info( "#" );
        log.info( "#  >>> Starting REDUCE phase..." );
        log.info( "#" );
        FileInputStream mapInput = new FileInputStream( tmp );
        AbstractStreamReducer reducer = (AbstractStreamReducer) reducerClass.newInstance();
        log.info( "#  - Starting reducer, output is: " + finalOutput.getAbsolutePath() );
        FileOutputStream fos = new FileOutputStream( finalOutput );
        reducer.reduce( mapInput, fos );
        fos.close();
        reduceTime = System.currentTimeMillis() - startReduce;
        log.info( "#  - REDUCE finished." );
        log.info( "#  - Output is in file {}", finalOutput.getAbsolutePath() );
        log.info( "#" );
        log.info( "#  Reducing required {} ms", reduceTime );
        log.info( "#" );
        log.info( "###########################################################################################" );
    }
    
    
    
    public File run() throws Exception {
    	
    	log.info( "#  Running 'map' on {} blocks using {} parallel mappers", dataFiles.size(), numberOfMappers );
    	List<File> mappedBlocks = doMap( dataFiles );
    	
    	log.info( "#  Running 'reduce' on {} results", mappedBlocks.size() );
    	doReduce( mappedBlocks, outputFile );
    	
    	log.info( "#  Ouput is written to {}", outputFile );
    	log.info( "#" );
        log.info( "###########################################################################################" );
        log.info( "# ");
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

    public static Thread createMapper( Class<?> mapClass, File inputFile, final File outFile ) throws Exception {
        final File file = inputFile;
        log.debug( "#  - Creating mapper for " + file.getAbsolutePath() );

        final InputStream in = new FileInputStream( file );
        final OutputStream out = new FileOutputStream( outFile );

        final AbstractStreamMapper map = (AbstractStreamMapper) mapClass.newInstance();
        Thread t = new Thread( new Runnable(){
            @Override
            public void run() {
                log.debug( "#  - Mapper " + file.getName() + " ~> " + outFile.getName() + " is starting..." );
                map.run( in, out );
                log.debug( "#  - Mapper " + file.getName() + " ~> " + outFile.getName() + " is finished" );
            }
        });
        return t;
    }


    /**
     * @param args
     */
    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {

        int maxMappers = 4;
        try {
            maxMappers = Integer.parseInt( System.getProperty( "max.mappers" ) );
        } catch (Exception e) {
            maxMappers = 4;
        }

        if( args.length < 3 ){
            log.info( "Usage:" );
            log.info( "   java stream.MapReduce map.class.name reduce.class.name file1 file2 file2  OUTPUT" );
        }

        log.info( "###########################################################################################" );
        log.info( "#" );
        log.info( "#  stream-mapred - A simple Map&Reduce Variant" );
        log.info( "#" );
        Class<?> clazz = findClass( args[0]);
        if( clazz == null ){
            System.err.println( "Cannot find MapperClass " + args[0] );
            System.exit( -1 );
        }
        log.info( "#   Mapper: " + args[0] );

        Class<?> reducerClass = findClass( args[1] );
        if( reducerClass == null ){
            System.err.println( "Cannod find ReducerClass " + args[1] );
            System.exit( -1 );
        }
        log.info( "#   Reducer: " + args[1] );

        List<Thread> mappers = new ArrayList<Thread>();
        File finalOutput = new File( args[ args.length - 1 ] );
        log.info( "#   Final output: " + finalOutput.getAbsolutePath() );
        log.info( "#" ); 
        final List<File> outputs = new ArrayList<File>();
        List<File> inputs = new ArrayList<File>();
        for( int i = 2; i < args.length -1; i++ ){
            File f = new File( args[i] );
            log.info( "#   adding input {}", f.getAbsolutePath() );
            inputs.add( f );
        }

        log.info( "#" );
        log.info( "#  >>> Starting MAP phase..." );
        log.info( "#" );
        long start = System.currentTimeMillis();
        long mapTime = 0L;

        while( ! mappers.isEmpty() || ! inputs.isEmpty() ){

            Iterator<Thread> it = mappers.iterator();
            while( it.hasNext() ){
                Thread t = it.next();
                if( ! t.isAlive() ){
                    log.debug( "#  Mapper {} finished, removing from list", t );
                    it.remove();
                }
            }

            while( mappers.size() < maxMappers && !inputs.isEmpty() ){
                File input = inputs.remove( 0 );
                log.debug( "#   Creating mapper for {}", input );
                File outputFile = createOutfile( input );
                outputs.add( outputFile );
                Thread t = createMapper( clazz, input, outputFile);
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
        log.info( "#  All mappers finished." );
        log.info( "# ");
        log.info( "#  >>> MAP phase complete." );
        log.info( "# ");

        log.info( "###########################################################################################" );
        log.info( "#" );
        File tmp = File.createTempFile( "map_reduce_tmp", "__" );
        log.info( "#   Creating intermediate output");
        log.info( "#   Temporary output is created in '{}'", tmp.getAbsolutePath() );
        //tmp.deleteOnExit();
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
            //mapped.delete();
        }
        intermediate.close();
        
        long startReduce = System.currentTimeMillis();
        long reduceTime = 0L;
        log.info( "#" );
        log.info( "###########################################################################################" );
        log.info( "#" );
        log.info( "#  >>> Starting REDUCE phase..." );
        log.info( "#" );
        FileInputStream mapInput = new FileInputStream( tmp );
        AbstractStreamReducer reducer = (AbstractStreamReducer) reducerClass.newInstance();
        log.info( "#  - Starting reducer, output is: " + finalOutput.getAbsolutePath() );
        FileOutputStream fos = new FileOutputStream( finalOutput );
        reducer.reduce( mapInput, fos );
        fos.close();
        reduceTime = System.currentTimeMillis() - startReduce;
        log.info( "#  - REDUCE finished." );
        log.info( "#  - Output is in file {}", finalOutput.getAbsolutePath() );
        log.info( "#" );
        log.info( "#" );
        log.info( "###########################################################################################" );
        log.info( "# ");
        log.info( "#  Map-Phase required:      {} ms", mapTime );
        log.info( "#  Reduce-phase required:   {} ms", reduceTime );
        log.info( "#  ------------------------------------------" );
        log.info( "#  Total processing time is {} ms", reduceTime + mapTime );
        log.info( "# ");
        log.info( "###########################################################################################" );
    }
}
