package stream.io;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import stream.data.Data;

public class SparseDataStreamWriter extends DataStreamWriter {

	/* This map provides a mapping of features to indexes 
	 * features with a numeric (integer) name will be mapped to their value directly */
	Map<String,Integer> indexes = new HashMap<String,Integer>();
	Integer largestIndex = 0;

	public SparseDataStreamWriter(OutputStream out) {
		super(out);
	}

	@Override
	public void writeHeader(Data datum) {
	}

	@Override
	public void write(Data datum) {

		StringBuffer annotation = new StringBuffer();
		
		for( String key : datum.keySet() ){

			try {
				String value = datum.get( key ).toString();
				if( value.indexOf( " " ) > 0 ) {
					value = "'" + value.replaceAll( "'", "\\\'" ) + "'";
				}
				
				p.print( " " );
				p.print( key );
				p.print( ":" );
				p.print( value );
			} catch (Exception e) {
				e.printStackTrace();
				log.warn( "Skipping non-numerical feature '{}'", key );
				annotation.append( " " );
				annotation.append( key );
				annotation.append( ":'" );
				annotation.append( lineEscape( datum.get( key ) ) );
				annotation.append( "'" );
			}
		}
		if( annotation.length() > 0 ){
			p.print( " #" );
			p.print( annotation.toString() );
		}
			
		p.println();
	}
	
	protected String lineEscape( Serializable val ){
		if( val == null )
			return "";
		
		String str = val.toString();
		return str.replaceAll( "'", "," ).replaceAll( "\\n", " " );
	}
}