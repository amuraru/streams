/**
 * 
 */
package com.rapidminer.stream.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.DataProcessor;
import stream.io.DataStream;
import stream.io.DataStreamWriter;
import stream.util.MacroExpander;

/**
 * @author chris
 *
 */
public class OperatorGenerator {

	static Logger log = LoggerFactory.getLogger( OperatorGenerator.class );


	public static OperatorInfo generate( Class<?> clazz, File outputDirectory ) throws Exception {

		Map<String,String> opts = new HashMap<String,String>();

		//Map<String,Class<?>> params = ParameterDiscovery.discoverParameters( clazz );
		String pkg = "com.rapidminer.stream.operators"; //.getPackage().getName();
		String className = clazz.getSimpleName() + "Operator";

		opts.put( "DATA_PROCESSOR_CLASS", clazz.getCanonicalName() );
		opts.put( "PACKAGE", pkg );
		opts.put( "CLASSNAME", className );

		String outDir = pkg.replaceAll( "\\.", File.separator );
		return generate( opts, new File( outputDirectory.getAbsoluteFile() + File.separator + outDir ) );
	}


	private static OperatorInfo generate( Map<String,String> opts, File outputDirectory ) throws Exception {

		outputDirectory.mkdirs();

		File source = new File( outputDirectory.getAbsolutePath() + File.separator + opts.get( "CLASSNAME" ) + ".java" );
		PrintStream out = new PrintStream( new FileOutputStream( source ) );

		URL url = OperatorGenerator.class.getResource( "/GenericOperator.template" );
		BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream() ) );

		MacroExpander macros = new MacroExpander( opts );

		log.trace( "#---------------------------------------------------------------------- " );
		log.info( "#  Generating new operator source file in '{}'", source );
		log.trace( "# " );
		log.trace( "# " );
		String line = reader.readLine();
		while( line != null ){
			String str = macros.expand( line );
			out.println( str );
			log.trace( "   {}", str );
			line = reader.readLine();
		}
		log.trace( "# " );
		log.trace( "#---------------------------------------------------------------------- " );

		out.close();
		return new OperatorInfo( "", opts.get( "CLASSNAME" ).replaceAll( "Operator$", "" ), opts.get( "PACKAGE" ) + "." + opts.get( "CLASSNAME" ) );
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		List<OperatorInfo> operators = new ArrayList<OperatorInfo>();

		File outDir = new File( "/tmp" );
		if( args.length > 0 )
			outDir = new File( args[0] );

		try {
			log.info( "Writing new Operators to {}", outDir );


			String[] pkgs = new String[]{
					"stream.data.mapper",
					"stream.data.test",
					"stream.learner"
			};

			int count = 0;
			for( String pkg : pkgs ){

				Class<?>[] classes = ClassFinder.getClasses( pkg );

				for( Class<?> clazz : classes ){
					
					String pkgName = clazz.getPackage().getName();
					if( ! pkgName.startsWith( pkg ) ){
						log.info( "Skipping class {}", clazz );
						continue;
					}
						
					
					log.debug( "Found class {} in package {}", clazz, clazz.getPackage().getName() );					

					log.trace( "Found class: {}", clazz );

					if( clazz.isInterface() ){
						log.debug( "Skipping code-generation for interface {}", clazz );
						continue;
					}
					
					if( DataStream.class.isAssignableFrom( clazz ) ){
						log.debug( "Skipping code-generation for i/o class {}", clazz );
						continue;
					}
					
					if( DataStreamWriter.class.isAssignableFrom( clazz ) ){
						log.debug( "Skipping code-generation for i/o class {}", clazz );
						continue;
					}
					
					if( ! DataProcessor.class.isAssignableFrom( clazz ) ){
						log.debug( "Skipping code-generator for non-DataProcessor class" );
						continue;
					}
					
					try {
						log.trace( "Ensuring that the class {} can be instantiated...", clazz );
						clazz.newInstance();
						OperatorInfo info = generate( clazz, outDir );
						operators.add( info );
						count++;
					} catch (Exception e) {
						log.debug( "Instantiation failed for class {}, skipping", clazz );
						continue;
					}
				}
			}
			
			log.debug( "{} operators generated.", count );
			
			Collections.sort( operators );
			
			
			OperatorList list = new OperatorList();
			list.add( operators );
			list.setDefaultGroup( "stream.operators" );
			
			if( args.length > 1 ){
				File opXml = new File( args[1] );
				log.info( "Reading base-operator list from {}", opXml );
				
				File outFile = new File( "src/main/resources/com/rapidminer/stream/resources/DataStreamOperators.xml" );
				log.info( "Writing operators list to {}", outFile );
				list.insertIntoOperatorsXml( opXml, outFile );
			}
			
			//generate( opts, new File( "/tmp" ) );

		} catch (Exception e) {
			log.error( "Error: {}", e.getMessage() );
			e.printStackTrace();
		}
	}
}