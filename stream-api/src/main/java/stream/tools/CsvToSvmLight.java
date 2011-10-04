/**
 * 
 */
package stream.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.mapper.BinaryLabels;
import stream.data.mapper.RemoveZeroes;
import stream.io.CsvStream;
import stream.io.SvmLightStreamWriter;
import stream.util.CommandLineArgs;

/**
 * @author chris
 *
 */
public class CsvToSvmLight {

	static Logger log = LoggerFactory.getLogger( CsvToSvmLight.class );
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		CommandLineArgs cla = new CommandLineArgs( args );
		if( cla.getArguments().size() != 2 ){
			System.out.println( "Usage:" );
			System.out.println( "       java stream.tools.CsvToSvmLight [OPTIONS] INPUT_FILE  OUTPUT_FILE" );
			System.out.println();
			System.exit( -1 );
		}
		
		Integer limit = new Integer( cla.getOption( "limit", Integer.MAX_VALUE + "" ) );
		String split = cla.getOption( "split", "(;|,)" );
		
		File inFile = new File( cla.getArguments().get( 0 ) );
		
		File outFile = null;
		if( cla.getArguments().size() > 1 )
			outFile = new File( cla.getArguments().get( 1 ) );
		else
			outFile = new File( cla.getArguments().get( 0 ) + ".svm_light" );
		
		OutputStream out = new FileOutputStream( outFile );
		System.out.println( "Converting " + inFile + " to " + outFile );
		CsvStream stream = new CsvStream( inFile.toURI().toURL(), split );
		stream.addPreprocessor( new BinaryLabels() );
		stream.addPreprocessor( new RemoveZeroes() );

		int count = 0;
		SvmLightStreamWriter writer = new SvmLightStreamWriter( out );
		Data item = stream.readNext();
		while( item != null && count < limit ){
			count++;
			if( count % 1000 == 0 ){
				System.out.println( count + " items converted." );
			}
			writer.write( item );
			item = stream.readNext( item );
		}
		writer.printMapping( new File( outFile.getAbsolutePath() + ".features" ) );
		writer.close();
	}
}