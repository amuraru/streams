package stream.learner;

import java.net.URL;

import org.junit.Test;

import stream.data.Data;
import stream.data.tree.TreeEdges;
import stream.io.CsvStream;
import stream.io.DataStreamProcessor;
import stream.io.DefaultTreeParser;

public class TreeParserTest {

	
	
	@Test
	public void test() throws Exception {
		
		URL url = TreeParserTest.class.getResource( "/sql-queries.tsv" );
		CsvStream input = new CsvStream( url, "\\t\\t\\t" );
		
		DataStreamProcessor preprocessor = new DataStreamProcessor( input );
		preprocessor.addDataProcessor( new DefaultTreeParser( "tree" ) );
		preprocessor.addDataProcessor( new TreeEdges() );
		
		
		Data x1 = preprocessor.readNext();
		Data x2 = preprocessor.readNext();
		
		EucleadianDistance ed = new EucleadianDistance();
		Double d = ed.distance( x1, x2 );
		System.out.println( "distance: " + d );
		
	}

}
