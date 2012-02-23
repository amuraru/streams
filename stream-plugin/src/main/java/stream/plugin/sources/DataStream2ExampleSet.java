/**
 * 
 */
package stream.plugin.sources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.plugin.DataSourceObject;

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

/**
 * @author chris
 *
 */
public class DataStream2ExampleSet extends Operator {
	
	static Logger log = LoggerFactory.getLogger( DataStream2ExampleSet.class );

	final InputPort input = getInputPorts().createPort( "stream" );
	
	final OutputPort output = getOutputPorts().createPort( "example set" );
	
	public final static String LIMIT_PARAMETER = "Limit";
	
	/**
	 * @param description
	 */
	public DataStream2ExampleSet(OperatorDescription description) {
		super(description);
	}

	
	/**
	 * @see com.rapidminer.operator.Operator#doWork()
	 */
	@Override
	public void doWork() throws OperatorException {
		
		DataSourceObject stream = input.getData( DataSourceObject.class );
		Map<String,Class<?>> attributes = new LinkedHashMap<String,Class<?>>();
		
		List<Data> items = new ArrayList<Data>();
		Data item = stream.readNext();
		while( item != null ){
			items.add( item );
			
			for( String key : item.keySet() ){
				
				Serializable s = item.get( key );
				if( Number.class.isAssignableFrom( s.getClass() ) ){
					attributes.put( key, Double.class );
				} else {
					attributes.put( key, String.class );
				}
			}
			
			item = stream.readNext();
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
		
		output.deliver( exampleSet );
	}
	
	
	
	
	public static ExampleSet createExampleSet( List<Data> items ){

		Map<String,Class<?>> attributes = new LinkedHashMap<String,Class<?>>();
		
		for( Data item : items ){
			items.add( item );
			
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