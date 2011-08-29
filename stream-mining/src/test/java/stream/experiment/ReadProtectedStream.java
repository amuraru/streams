/**
 * 
 */
package stream.experiment;

import stream.data.Data;
import stream.io.DataSource;
import stream.io.DataStream;

/**
 * @author chris
 *
 */
public class ReadProtectedStream {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		DataSource ds = new DataSource();
		ds.setName( "jwall-logs" );
		ds.setUrl( "http://kirmes.cs.uni-dortmund.de/data/logs/sql-logs.csv" );
		ds.setClassName( "stream.io.CsvStream" );
		
		DataStream stream = ds.createDataStream();
		int i = 10;
		Data data = stream.readNext();
		
		while( data != null && i > 0 ){
			System.out.println( "Data: " + data );
			data = stream.readNext();
		}
	}
}