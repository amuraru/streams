package stream.io;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

public class TrafficLabelExtraction {

	static Logger log = LoggerFactory.getLogger( TrafficLabelExtraction.class );
	final static SimpleDateFormat fmt = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ssZ" );

	public static Long parseDate( String data ){
		try {
			Date date = fmt.parse( data + "00" );
			return date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1L;
	}


	public static void extractLabels( URL url ){
		try {
			DataStream stream = new CsvStream( url, ";" );

			int limit = 10;
			Data item = stream.readNext();
			while( limit-- > 0 ){
				log.info( "data: {}", item );

				Long zaehler = new Long( item.get( "zs" ) + "" );
				Long time = parseDate( item.get( "zeit" ) + "" );
				log.info( "   {} @ {}", zaehler, time );

				item = stream.readNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL url = new URL( "file:/Users/chris/dinav_zs_data2010_07_01.csv" );

		if( args.length > 0 ){
			File in = new File( args[0] );
			
			if( in.isDirectory() ){
				
				for( File file : in.listFiles() ){
					if( file.isFile() && file.getName().endsWith( ".csv" ) ){
						extractLabels( new URL( "file:" + file.getAbsolutePath() ) );
					}
				}
				
			} else {
				url = new URL( "file:" + in.getAbsolutePath() );
			}
		}

		extractLabels( url );
	}
}