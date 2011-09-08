package stream.tools;
import java.net.URL;
import java.rmi.Naming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStream;
import stream.io.SvmLightDataStream;
import stream.remote.DataIndexService;


public class DataUpload {

	static Logger log = LoggerFactory.getLogger( DataUpload.class );

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		URL url = null;
		String remoteUrl = "rmi://kirmes.cs.uni-dortmund.de:9106/DataIndex";

		if( args.length > 0 )
			url = new URL( args[0] );
		else
			url = new URL( "http://kirmes.cs.uni-dortmund.de/data/adult.tr" );

		if( args.length > 1 )
			remoteUrl = args[1];

		DataStream stream = new SvmLightDataStream( url );

		String dataSet = "adult.tr";
		String path = url.getPath();
		if( path.lastIndexOf( "/" ) > 0 && ! path.endsWith( "/" ) ){
			dataSet = path.substring( path.lastIndexOf( "/" ) + 1 );
		}

		DataIndexService index = (DataIndexService) Naming.lookup( remoteUrl );
		int limit = 10000;
		int i = 0;
		Data item = stream.readNext();
		while( item != null && limit-- > 0 ){

			if( i > 0 && i % 100 == 0 ){
				log.info( "{} items inserted.", i );
			}

			index.insert( dataSet, item );
			item = stream.readNext();
			i++;
		}
	}
}
