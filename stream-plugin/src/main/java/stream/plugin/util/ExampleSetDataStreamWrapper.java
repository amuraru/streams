/**
 * 
 */
package stream.plugin.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;
import stream.io.DataStream;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.tools.Ontology;

/**
 * @author chris
 *
 */
public class ExampleSetDataStreamWrapper implements DataStream {

	Map<String,Class<?>> types;
	Map<String,Attribute> attributes;
	ExampleSet exampleSet;
	int idx = 0;
	Iterator<Example> examples;
	
	public ExampleSetDataStreamWrapper( ExampleSet exampleSet ){
		this.exampleSet = exampleSet;
		
		types = new LinkedHashMap<String,Class<?>>();
		attributes = new LinkedHashMap<String,Attribute>();
		
		Iterator<Attribute> it = exampleSet.getAttributes().allAttributes();
		
		while( it.hasNext() ){
			
			Attribute attr = it.next();
			AttributeRole role = exampleSet.getAttributes().getRole( attr );
			String name = attr.getName();

			attributes.put( attr.getName(), attr );
			
			if( role.isSpecial() && ! name.startsWith( "@" ) ){
				name = "@" + name;
			}
			
			Class<?> type = String.class;
			
			switch( attr.getValueType() ){
				case Ontology.NUMERICAL:
					type = Double.class;
			}
			
			types.put( name, type );
		}
		
		examples = this.exampleSet.iterator();
	}
	
	public String mapAttributeName( Attribute attribute, Attributes attributes ){
		String name = attribute.getName();
		AttributeRole role = attributes.getRole( attribute );
		
		if( role.isSpecial() && ! name.startsWith( "@" ) ){
			return "@" + name;
		}
		
		
		return name;
	}
	
	
	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return types;
	}
	

	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext( new DataImpl() );
	}

	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {

		while( examples.hasNext() ){
			return this.wrap( datum, examples.next() );
		}

		return null;
	}

	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see stream.io.DataStream#addPreprocessor(stream.data.DataProcessor)
	 */
	@Override
	public void addPreprocessor(DataProcessor proc) {
		throw new RuntimeException( "Not supported!" );
	}

	/**
	 * @see stream.io.DataStream#addPreprocessor(int, stream.data.DataProcessor)
	 */
	@Override
	public void addPreprocessor(int idx, DataProcessor proc) {
		throw new RuntimeException( "Not supported!" );
	}

	/**
	 * @see stream.io.DataStream#getPreprocessors()
	 */
	@Override
	public List<DataProcessor> getPreprocessors() {
		return new ArrayList<DataProcessor>();
	}

	protected Data wrap( Data item, Example example ){
		
		for( String name : attributes.keySet() ){
			
			Attribute attribute = attributes.get( name );
			String key = mapAttributeName( attribute, example.getAttributes() );
			
			if( attribute.getValueType() == Ontology.NUMERICAL ){
				double d = example.getValue( attribute );
				item.put( key, new Double( d ) );
			} else {
				item.put( key, example.getValueAsString( attribute ) );
			}
		}
		
		return item;
	}
}