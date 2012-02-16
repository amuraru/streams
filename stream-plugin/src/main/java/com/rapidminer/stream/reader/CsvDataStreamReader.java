/**
 * 
 */
package com.rapidminer.stream.reader;

import java.util.List;
import java.util.Map;

import stream.io.CsvStream;
import stream.tools.StreamRunner;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;

/**
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 * 
 */
public class CsvDataStreamReader extends DataStreamReader<CsvStream> {
	
	public final static String SEPARATOR_KEY = "separator"; 
	
	/**
	 * @param description
	 */
	public CsvDataStreamReader(OperatorDescription description ) {
		super(description, stream.io.CsvStream.class );
	}
	
	
	/**
	 * @see com.rapidminer.stream.reader.DataStreamReader#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeString( SEPARATOR_KEY, "The split separator", ";" ) );
		return types;
	}





	public CsvStream createDataStream( Class<CsvStream> dataStreamClass, Map<String,String> parameters ) throws Exception {
		parameters.put( "class", dataStreamClass.getName() );
		CsvStream stream = (CsvStream) StreamRunner.createStream( parameters );
		return stream;
		
		
		/*
		Constructor<CsvStream> construct = dataStreamClass.getConstructor( URL.class );
		
		URL source = null;
		
		Map<String,String> params = OperatorUtils.getParameters( this );
		
		String url = params.get( INPUT_FILE );
		try {
			source = new URL( url );
		} catch (Exception e) {
			source = new URL( "file:" + url );
		}
		
		CsvStream stream = construct.newInstance( source );
		ParameterInjection.inject( stream, parameters );
		return stream;
		 */
	}
}
