package stream.io;

import java.util.ArrayList;
import java.util.List;

import stream.data.Data;
import stream.data.DataImpl;

public class DataStreamLoopTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		List<Data> items = new ArrayList<Data>();
		for( int i = 0; i < 10; i++ ){
			Data item = new DataImpl();
			item.put( "@id", i + "" );
			item.put( "x", Math.random() );
			items.add( item );
		}
		
		DataStreamLoop loop = new DataStreamLoop();
		loop.setSource( new ListDataStream( items ) );
		loop.setRepeat( 2 );
		loop.setShuffle( true );
		
		List<Data> result = new ArrayList<Data>();
		Data datum = loop.readNext();
		while( datum != null ){
			result.add( datum );
			System.out.println( datum );
			datum = loop.readNext();
		}
		
		System.out.println( "loop resulted in " + result.size() + " items..." );
	}

}
