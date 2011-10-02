package stream.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import stream.data.Data;
import stream.data.DataUtils;

public class SvmLightStreamWriter extends DataStreamWriter {

	/* This map provides a mapping of features to indexes 
	 * features with a numeric (integer) name will be mapped to their value directly */
	Map<String,Integer> indexes = new HashMap<String,Integer>();
	Integer largestIndex = 0;

	public SvmLightStreamWriter(OutputStream out) {
		super(out);
	}

	@Override
	public void writeHeader(Data datum) {
	}

	@Override
	public void write(Data datum) {

		Serializable label = datum.get( "@label" );
		if( label == null ){
			log.error( "SvmLightStreamWriter does only support writing labeled data!" );
			log.error( "Skipping datum {}", datum );
			return;
		}

		p.print( label );

		StringBuffer annotation = new StringBuffer();
		
		for( String key : DataUtils.getKeys( datum ) ){

			try {
				Double value = new Double( datum.get( key ).toString() );
				p.print( " " );
				Integer index = -1;
				if( key.matches( "\\d+" ) ){
					index = new Integer( key );
				} else {
					index = this.indexes.get( key );
					if( index == null ){
						index = largestIndex + 1;
						indexes.put( key, index );
					}
				}

				if( largestIndex < index )
					largestIndex = index;

				p.print( index );
				p.print( ":" );
				p.print( value );
			} catch (Exception e) {
				log.debug( "Skipping non-numerical feature '{}'", key );
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
	
	
	
	public void printMapping( File file ) throws Exception {
		FileOutputStream fos = new FileOutputStream( file );
		printMapping( fos );
		fos.close();
	}
	
	
	public void printMapping( OutputStream out ){
		PrintStream p = new PrintStream( out );
		p.println( "#feature,index" );
		for( String key : indexes.keySet() ){
			p.println( key + "," + indexes.get( key ) );
		}
		p.flush();
		p.close();
	}
}