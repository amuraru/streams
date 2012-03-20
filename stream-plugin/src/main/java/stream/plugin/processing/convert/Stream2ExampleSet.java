/**
 * 
 */
package stream.plugin.processing.convert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
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
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.att.AttributeSet;


/**
 * @author chris
 *
 */
public class Stream2ExampleSet extends Operator {

	static Logger log = LoggerFactory.getLogger( Stream2ExampleSet.class );
	
	public final static String BUFFER_SIZE_PARAMETER = "bufferSize";
	
	final InputPort input = getInputPorts().createPort( "data item" );
	final OutputPort output = getOutputPorts().createPort( "example set" );
	final OutputPort passThroughPort = getOutputPorts().createPort( "data item" );
	
	boolean doSetup = true;
	int bufferSize = 100;
	final List<Data> buffer = new ArrayList<Data>( bufferSize );
	
	
	/**
	 * @param description
	 */
	public Stream2ExampleSet(OperatorDescription description) {
		super(description);
		acceptsInput( DataObject.class );
		producesOutput( ExampleSet.class );
		producesOutput( DataObject.class );
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		
		if( doSetup ){
			bufferSize = this.getParameterAsInt( BUFFER_SIZE_PARAMETER );
			buffer.clear();
			doSetup = false;
		}
		
		DataObject event = input.getData( DataObject.class );

		if( buffer.size() < bufferSize ){
			
			log.debug( "Buffer not full, yet, enqueuing data-item..." );
			buffer.add( event.getWrappedDataItem() );
			
		} else {
			
			log.debug( "buffer-size reached, emitting example-set created from {} items", buffer.size() );
			ExampleSet exampleSet = createExampleSet( buffer );
			buffer.clear();
			output.deliver( exampleSet );
		}
		
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
	
	
	

	/**
	 * @see com.rapidminer.operator.Operator#getParameterTypes()
	 */
	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		//types.add( new ParameterTypeString( DATA_KEY_PARAMETER, "Attribute name to convert", "Data" ) );
		//types.add( new ParameterTypeInt( ROWS_PARAMETER, "Number of rows ", 1, Integer.MAX_VALUE, 1440 ) );
		types.add( new ParameterTypeInt( BUFFER_SIZE_PARAMETER, "The number of events collected before creating an example-set", 1, Integer.MAX_VALUE, 100 ) );
		return types;
	}
}