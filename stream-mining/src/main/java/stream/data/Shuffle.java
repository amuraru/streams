package stream.data;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import stream.io.CsvStream;
import stream.io.DataStreamWriter;

public class Shuffle {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		URL url = new URL( "file:///Users/chris/iris.csv" );
		CsvStream stream = new CsvStream( url );
		
		List<Data> items = new ArrayList<Data>();
		Data item = stream.readNext();
		while( item != null ){
			items.add( item );
			item = stream.readNext();
		}
		
		Collections.shuffle( items, new Random( System.currentTimeMillis() ) );
		DataStreamWriter writer = new DataStreamWriter( new File( "/Users/chris/iris-shuffled.csv" ), ";" );
		
		for( Data data : items ){
			writer.dataArrived( data );
		}
		writer.close();
	}
}
