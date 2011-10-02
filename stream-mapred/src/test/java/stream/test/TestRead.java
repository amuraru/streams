/**
 * 
 */
package stream.test;

import java.io.FileOutputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.mapper.BinaryLabels;
import stream.data.mapper.RemoveZeroes;
import stream.io.CsvStream;
import stream.io.SvmLightStreamWriter;

/**
 * @author chris
 *
 */
public class TestRead {

	static Logger log = LoggerFactory.getLogger( TestRead.class );

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL url = TestRead.class.getResource( "/kddcup" );

		CsvStream stream = new CsvStream( url );
		stream.addPreprocessor( new BinaryLabels() );
		stream.addPreprocessor( new RemoveZeroes() );

		int count = 0;
		SvmLightStreamWriter writer = new SvmLightStreamWriter( new FileOutputStream( "/data/kddcup.svmlight" ) );
		Data item = stream.readNext();
		log.info( "Read:\n{}", item );
		while( item != null && count < 1){
			count++;
			if( count % 1000 == 0 ){
				log.info( "{} items converted.", count );
			}
			writer.write( item );
			item = stream.readNext();
		}
	}

}
