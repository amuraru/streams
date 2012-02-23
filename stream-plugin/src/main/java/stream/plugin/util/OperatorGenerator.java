/**
 * 
 */
package stream.plugin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.DataProcessor;
import stream.io.DataStream;
import stream.io.DataStreamWriter;
import stream.tools.URLUtilities;
import stream.util.Description;
import stream.util.MacroExpander;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * @author chris
 *
 */
public class OperatorGenerator {

	static Logger log = LoggerFactory.getLogger( OperatorGenerator.class );

	Map<String,String> groupMap = new HashMap<String,String>();
	Map<String,String> packageMap = new HashMap<String,String>();
	Map<String,String> wrappers = new HashMap<String,String>();

	Set<String> ignoreList = new HashSet<String>();
	Map<Class<?>,String> templates = new LinkedHashMap<Class<?>,String>();
	
	public OperatorGenerator(){
		packageMap.put( "stream.data.mapper", "stream.plugin.processing.transform" );
		packageMap.put( "fact.io", "fact.plugin.operators.io" );
		packageMap.put( "fact.data", "fact.plugin.operators.data" );
		packageMap.put( "fact.image", "fact.plugin.operators.image" );

		groupMap.put( "", "Data Stream.Processing" );
		groupMap.put( "stream.io", "Data Stream.Sources" );
		groupMap.put( "stream.preprocessing", "Data Stream.Processing.Transformations" );
		groupMap.put( "stream.data.mapper", "Data Stream.Processing.Transformations" );
		groupMap.put( "stream.data.preprocessing", "Data Stream.Processing.Transformations" );
		groupMap.put( "stream.preprocessing.data", "Data Stream.Processing.Transformations" );
		
		templates.put( stream.io.DataStream.class, "/GenericStreamReader.template" );
		templates.put( stream.data.DataProcessor.class, "/GenericOperator.template" );
		templates.put( stream.io.DataStreamWriter.class, "/GenericStreamWriter.template" );

		wrappers.put( "stream.data.DataProcessor", "/GenericOperator.template" );
		
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( OperatorGenerator.class.getResourceAsStream( "/ignore-classes.txt" ) ) );
			String line = reader.readLine();
			while( line != null ){
				ignoreList.add( line.trim() );
				log.info( "Adding {} to ignore-list...", line.trim() );
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			log.error( "Failed to read ignore-classes.txt" );
		}
	}


	public String mapGroup( String grp ){
		if( groupMap.containsKey( grp ) ){
			log.debug( "Mapping group {} -> {}", grp, groupMap.get( grp ) );
			return groupMap.get( grp );
		}

		log.debug( "No mapping found for group '{}'", grp );
		return grp;
	}


	public String mapPackage( String pkg ){
		if( packageMap.containsKey( pkg ) ){
			log.debug( "Mapping package {} -> {}", pkg, packageMap.get( pkg ) );
			return packageMap.get( pkg );
		}
		log.debug( "No mapping for package '{}'", pkg );
		return pkg;
	}



	public OperatorInfo generate( Class<?> clazz, File outputDirectory ) throws Exception {

		Map<String,String> opts = new HashMap<String,String>();

		//Map<String,Class<?>> params = ParameterDiscovery.discoverParameters( clazz );
		//String pkg = "stream.plugin.operators"; //.getPackage().getName();
		String className = clazz.getSimpleName() + "Operator";

		String pkg = mapPackage( clazz.getPackage().getName() );

		opts.put( "GROUP", mapGroup( clazz.getPackage().getName() ) );
		opts.put( "DATA_PROCESSOR_CLASS", clazz.getCanonicalName() );
		opts.put( "DATA_STREAM_CLASS", clazz.getCanonicalName() );
		opts.put( "PACKAGE", pkg );
		opts.put( "CLASSNAME", className );

		
		Description desc = clazz.getAnnotation( Description.class );
		if( desc != null ){
			String grp = desc.group();
			log.info( "Annotated group is {}", grp );
			if( grp != null ){
				grp = mapGroup( grp );
				opts.put( "GROUP", grp );
			}
			
			if( desc.name() != null && ! "".equals( desc.name().trim() ) ){
				opts.put( "NAME", desc.name().trim() );
			}
		}

		String template = null; // "/GenericOperator.template";
		
		for( Class<?> tmpl : templates.keySet() ){
			if( tmpl.isAssignableFrom( clazz ) ){
				log.info( "Using template '{}' for class '{}'", templates.get( tmpl ), clazz );
				template = templates.get( tmpl );
				break;
			}
		}
		
		if( template == null ){
			log.error( "No template found for class {}", clazz );
			return null;
		}
		
		String outDir = pkg.replaceAll( "\\.", File.separator );
		log.debug( "Generating java class {} in package {}", className, opts.get( "PACKAGE" ) );
		OperatorInfo info = generate( opts, new File( outputDirectory.getAbsoluteFile() + File.separator + outDir ), template );
		
		
		String doc = "/" + clazz.getCanonicalName().replaceAll( "\\.", "/" ) + ".md";
		URL url = OperatorGenerator.class.getResource( doc );
		if( url != null ){
			String txt = URLUtilities.readContent( url );
			log.info( "Found documentation at {}", url );
			
			MarkdownProcessor markdown = new MarkdownProcessor();
			String html = markdown.markdown( txt );
			log.info( "Html documentation:\n{}", html );
			info.setDocText( html );
		}
		
		return info;
	}


	private static OperatorInfo generate( Map<String,String> opts, File outputDirectory, String template ) throws Exception {

		outputDirectory.mkdirs();

		File source = new File( outputDirectory.getAbsolutePath() + File.separator + opts.get( "CLASSNAME" ) + ".java" );
		PrintStream out = new PrintStream( new FileOutputStream( source ) );

		String tmpl = template;
		if( ! tmpl.startsWith( "/" ) )
			tmpl = "/" + template;
		URL url = OperatorGenerator.class.getResource( tmpl );
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
		
		String name = opts.get( "CLASSNAME" ).replaceAll( "Operator$", "" );
		if( opts.get( "NAME" ) != null )
			name = opts.get( "NAME" );
		
		return new OperatorInfo( opts.get( "GROUP" ), name, opts.get( "PACKAGE" ) + "." + opts.get( "CLASSNAME" ) );
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		OperatorGenerator gen = new OperatorGenerator();
		
		
		List<OperatorInfo> operators = new ArrayList<OperatorInfo>();

		File outDir = new File( "/tmp" );
		if( args.length > 0 )
			outDir = new File( args[0] );

		
		OperatorList list = new OperatorList();
		list.setDefaultGroup( "Data Stream.Processing" );
		
		
		for( int i = 1; i < args.length - 1; i++ ){
			String inheritFrom = args[i];
			log.info( "Adding operators from {}", args[i] );
			try {
				String source = "";
				InputStream in = null;
				URL coreSource = OperatorGenerator.class.getResource( inheritFrom ); //"/stream/plugin/resources/DataStreamOperators-core.xml" );
				log.info( "Reading inherited-operators from {}", coreSource );
				
				if( coreSource == null ){
					log.info( "No inherited operators found in classpath for {}", inheritFrom );
					File file = new File( args[i] );
					if( ! file.canRead() ){
						log.info( "{} is not a readable file...", file );
						source = "'none'";
					} else {
						source = file.getAbsolutePath();
						in = new FileInputStream( file );
					}
				} else {
					source = coreSource.toString();
					in = coreSource.openStream();
				}
				
				if( i == 1 ){
					log.info( "Creating initial operators.xml from {}", source );
					list = new OperatorList( in );
					log.info( "   initial list has {} operators", list.getOperators().size() );
				} else {
					OperatorList inherited = new OperatorList( in );
					log.info( "Adding {} operators from {}", inherited.getOperators().size(), source );
					list.add( inherited.getOperators() );
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		try {
			log.info( "Writing new Operators to {}", outDir );


			String[] pkgs = new String[]{
					"stream.io",
					"stream.data",
					"stream.data.mapper",
					"stream.data.test",
					"stream.learner"
			};
			
			
			if( System.getProperty( "packages" ) != null ){
				pkgs = System.getProperty( "packages" ).split( "," );
			}
			log.info( "Checking for processors in {}", pkgs );

			int count = 0;
			for( String pkg : pkgs ){
				log.info( "Checking package {}", pkg );
				Class<?>[] classes = ClassFinder.getClasses( pkg );

				for( Class<?> clazz : classes ){

					if( gen.ignoreList.contains( clazz.getName() ) ){
						log.info( "Ignoring class {}", clazz );
						continue;
					}
					
					
					String pkgName = clazz.getPackage().getName();
					if( ! pkgName.startsWith( pkg ) ){
						log.info( "Skipping class {}", clazz );
						continue;
					}


					log.debug( "Found class {} in package {}", clazz, clazz.getPackage().getName() );					

					log.trace( "Found class: {}", clazz );

					if( clazz.isInterface() || Modifier.isAbstract( clazz.getModifiers() ) ){
						log.debug( "Skipping code-generation for interface/abstract class {}", clazz );
						continue;
					}

					if( DataStream.class.isAssignableFrom( clazz ) ){
						//log.info( "Skipping code-generation for i/o class {}", clazz );
						//continue;
						log.debug( "Found DataStream.class... {}", clazz );
					}

					if( DataStreamWriter.class.isAssignableFrom( clazz ) ){
						log.debug( "Skipping code-generation for i/o class {}", clazz );
						continue;
					}

					if( ! DataProcessor.class.isAssignableFrom( clazz ) && ! DataStream.class.isAssignableFrom( clazz ) ){
						log.debug( "Skipping code-generator for non-DataProcessor class" );
						continue;
					}
					
					if( DataProcessor.class.isAssignableFrom( clazz ) && !clazz.isAnnotationPresent( Description.class ) ){
						log.info( "Skipping DataProcessor '{}' with missing annotations...", clazz );
						continue;
					}

					try {
						log.trace( "Ensuring that the class {} can be instantiated...", clazz );
						
						if( DataStream.class.isAssignableFrom( clazz ) ){
							Constructor<?> con = clazz.getConstructor( URL.class );
							if( con == null ){
								throw new Exception( "DataStream class '" + clazz.getName() + "' does not provide URL-arg constructor!" );
							}
						} else 
							clazz.newInstance();
						
						log.info( "Running code-generation for class {}", clazz.getName() );
						OperatorInfo info = gen.generate( clazz, outDir );
						if( info != null ){
							log.info( "Adding operator {}", info );
							operators.add( info );
							count++;
						}
					} catch (Exception e) {
						log.error( "Instantiation failed for class {}: {}", clazz, e.getMessage() );
						continue;
					}
				}
			}

			log.info( "{} operators generated.", count );

			Collections.sort( operators );



			log.info( "Adding {} operators from operator-generator", operators.size() );
			list.add( operators );

			File outFile = new File( args[ args.length - 1 ] );
			log.info( "Writing operators list to {}", outFile );
			list.writeOperatorsXml( new FileOutputStream( outFile ) );
			
			File docXml = new File( outFile.getAbsolutePath().replaceAll( ".xml", "Doc.xml" ) );
			log.info( "Writing operators-doc to {}", docXml );
			list.writerOperatorsDocXml( new FileOutputStream( docXml ) );

		} catch (Exception e) {
			log.error( "Error: {}", e.getMessage() );
			e.printStackTrace();
		}
	}
}