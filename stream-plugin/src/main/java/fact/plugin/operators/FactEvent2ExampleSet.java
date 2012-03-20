/**
 * 
 */
package fact.plugin.operators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

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
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.att.AttributeSet;

import fact.data.EventExpander;
import fact.data.FactEvent;
import fact.plugin.FactEventObject;


/**
 * @author chris
 *
 */
public class FactEvent2ExampleSet extends Operator {

	static Logger log = LoggerFactory.getLogger( Array2ExampleSet.class );
	
	final InputPort input = getInputPorts().createPort( "evt fact event" );
	final OutputPort output = getOutputPorts().createPort( "example set" );
	final OutputPort passThroughPort = getOutputPorts().createPort( "evt fact event" );
	
	
	/**
	 * @param description
	 */
	public FactEvent2ExampleSet(OperatorDescription description) {
		super(description);
		acceptsInput( FactEventObject.class );
		producesOutput( ExampleSet.class );
		
	}


	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		FactEventObject event = input.getData( FactEventObject.class );
		
		float[] pixels = (float[]) event.get( FactEvent.DATA_KEY );
		int roi = pixels.length / 1440;
		
		List<Data> rows = EventExpander.expand( event, 1440, FactEvent.DATA_KEY, 0, roi );
	
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
		}
		
		return exampleSet;
	}
}