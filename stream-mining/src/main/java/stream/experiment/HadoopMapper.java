package stream.experiment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;
import stream.io.SvmLightDataStream;

public class HadoopMapper
implements DataProcessor
{
	public Data parse( String line ) throws Exception {
		return SvmLightDataStream.parseLine( new DataImpl(), line );
	}

	@Override
	public Data process(Data data) {
		return data;
	}

	public final void processInput( InputStream in ) throws Exception {
		BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
		try {
			String line = reader.readLine();
			while( line != null ){
				Data item = parse( line );
				process( item );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeResults( OutputStream out ) throws Exception {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		HadoopMapper mapper = new HadoopMapper();
		try {
			mapper.processInput( System.in );
			mapper.writeResults( System.out );
		} catch (Exception e) {
			System.err.println( e.getMessage() );
		}
		System.exit(0);
	}
}
