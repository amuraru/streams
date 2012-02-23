/**
 * 
 */
package stream.plugin.processing.convert;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataUtils;
import stream.plugin.DataObject;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.att.AttributeSet;


/**
 * @author chris
 *
 */
public class Array2ExampleSet extends Operator {

	static Logger log = LoggerFactory.getLogger( Array2ExampleSet.class );
	
	public final static String DATA_KEY_PARAMETER = "Key";
	public final static String ROWS_PARAMETER = "Rows";
	
	final InputPort input = getInputPorts().createPort( "data item" );
	final OutputPort output = getOutputPorts().createPort( "example set" );
	final OutputPort passThroughPort = getOutputPorts().createPort( "data item" );
	
	
	/**
	 * @param description
	 */
	public Array2ExampleSet(OperatorDescription description) {
		super(description);
		acceptsInput( DataObject.class );
		producesOutput( ExampleSet.class );
		
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		DataObject event = input.getData( DataObject.class );
		
		Integer numberOfRows = getParameterAsInt( ROWS_PARAMETER );
		if( numberOfRows == null ){
			throw new UserError( this, "Missing parameter '" + ROWS_PARAMETER + "'", -1 );
		}
		
		String key = getParameterAsString( DATA_KEY_PARAMETER );
		Serializable data = event.get( key );
		if( data == null ){
			throw new UserError( this, "No data found for key '" + key + "'!", -1 );
		}
		
		if( ! data.getClass().isArray() ){
			throw new UserError( this, new Exception( "" ), -1 );
		}
		
		int arrayLength = Array.getLength( data );
		if( arrayLength % numberOfRows > 0 ){
			throw new UserError( this, "Number of rows does not properly divide array-length!", -1 );
		}
		
		
		int roi = arrayLength / numberOfRows;
		List<Data> rows = expand( event, numberOfRows, key, 0, roi );
	
		ExampleSet exampleSet = createExampleSet( rows );
		output.deliver( exampleSet );
		passThroughPort.deliver( event );
	}
	
	
	
	public static ExampleSet createExampleSet( List<Data> items ){

		Map<String,Class<?>> attributes = new LinkedHashMap<String,Class<?>>();
		
		for( Data item : items ){
			for( String key : item.keySet() ){
				
				Serializable s = item.get( key );
				if( Number.class.isAssignableFrom( s.getClass() ) ){
					attributes.put( key, Double.class );
				} else {
					attributes.put( key, String.class );
				}
			}
		}
		
		log.debug( "Incoming data stream contains {} examples", items.size() );
		
		AttributeSet columns = new AttributeSet();
		Attribute[] attributeArray = new Attribute[ attributes.size() ];
		
		MemoryExampleTable table = new MemoryExampleTable();
		int i = 0;
		for( String key : attributes.keySet() ){
			
			int type = Ontology.NUMERICAL;
			
			if( String.class.equals( attributes.get( key ) ) ){
				type = Ontology.NOMINAL;
			}
			
			Attribute attr = AttributeFactory.createAttribute( key, type );
			columns.addAttribute( attr );
			table.addAttribute( attr );
			attributeArray[i++] = attr;
		}
		
		DataRowFactory drf = new DataRowFactory( DataRowFactory.TYPE_DOUBLE_ARRAY, '.' );
		
		for( Data datum : items ){
			String[] data = new String[ attributeArray.length ];
			i = 0;
			for( String key : attributes.keySet() ){
				
				if( datum.get( key ) == null )
					data[i] = "?";
				else
					data[i] = datum.get( key ).toString();
				i++;
			}
			
			while( i < attributeArray.length )
				data[i++] = "?";
			
			table.addDataRow(drf.create( data, attributeArray ) );
		}
		
		ExampleSet exampleSet = table.createExampleSet();
		
		//Attributes attributeSet = exampleSet.getAttributes();
		List<Attribute> attributeSet = new ArrayList<Attribute>();
		for( Attribute attr : exampleSet.getAttributes() )
			attributeSet.add( attr );
		
		for( Attribute attr : attributeSet ){
			if( attr.getName().startsWith( "@id" ) ){
				exampleSet.getAttributes().setId( attr );
				continue;
			}
			
			if( attr.getName().startsWith( "@label" ) ){
				exampleSet.getAttributes().setLabel( attr );
				continue;
			}
			
			if( DataUtils.isAnnotation( attr.getName() ) ){
				exampleSet.getAttributes().setSpecialAttribute( attr, attr.getName() );
			}
		}
		
		return exampleSet;
	}
	
	
	

	public static List<Data> expand( Data event, int numberOfPixels, String dataKey, int fromSlice, int toSlice ){
		
		List<Data> pixels = new ArrayList<Data>( numberOfPixels );
		
		
		Serializable value = event.get( dataKey );
		if( value == null ){
			return pixels;
		}
		
		if( ! value.getClass().isArray() ){
			throw new RuntimeException( "Object for key '" + dataKey + "' is not an array!" );
		}

		int roi = Array.getLength( value ) / numberOfPixels;
		DecimalFormat df = new DecimalFormat( dataKey + "_000" );
		
		for( int i = 0; i < numberOfPixels; i++ ){
			Data pixel = new DataImpl();
			pixel.put( "@chid", "" + i );
			
			for( int j = 0; j < roi; j++ ){
				if( j >= fromSlice && j <= toSlice ){
					String key = df.format( j );
					String val = Array.get( value, i * roi + j ) + "";
					try {
						Double d = new Double( val );
						pixel.put( key, d );
					} catch (Exception e) {
						pixel.put( key, val );
					}
				}
			}
			
			pixels.add( pixel );
		}
		
		
		
		return pixels;
	}
	


	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add( new ParameterTypeString( DATA_KEY_PARAMETER, "Attribute name to convert", "Data" ) );
		types.add( new ParameterTypeInt( ROWS_PARAMETER, "Number of rows ", 1, Integer.MAX_VALUE, 1440 ) );
		return types;
	}
}