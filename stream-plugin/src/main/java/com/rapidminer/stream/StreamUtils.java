package com.rapidminer.stream;

import stream.data.DataImpl;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;


/**
 * This class provides some utility functions such as wrapping examples into
 * Data-objects (IOObject).
 * 
 * @author Christian Bockermann
 *
 */
public class StreamUtils {

	/**
	 * This method wraps the given example set into an implementation of an IOObject
	 * and the Data interface.
	 * 
	 * @param example
	 * @return
	 */
	public static com.rapidminer.stream.DataObject wrap( Example example ){
		
		DataImpl item = new DataImpl();
		
		for( Attribute attr : example.getAttributes() ){
			String name = attr.getName();

			if( attr.isNumerical() ){
				item.put( name, example.getValue( attr ) );
			} else {
				item.put( name, example.getValueAsString( attr ) );
			}
		}
		
		return new com.rapidminer.stream.DataObject( item );
	}
}
