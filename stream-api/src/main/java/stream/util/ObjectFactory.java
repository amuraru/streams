/**
 * 
 */
package stream.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author chris
 *
 */
public class ObjectFactory 
	extends VariableContext
{
	static Map<String,Integer> globalObjectNumbers = new HashMap<String,Integer>();
	final static Map<String,String> classNames = new HashMap<String,String>();
	
	
	
	private ObjectFactory(){
		super( new HashMap<String,String>() );
	}
	
	
	public static void register( String name, Class<?> clazz ){
		classNames.put( name.toLowerCase(), clazz.getName() );
	}
	
	
	public static ObjectFactory newInstance(){
		return new ObjectFactory();
	}
	
	
	public static synchronized String getNextIdentifier( String obj ){
		Integer cur = globalObjectNumbers.get( obj );
		if( cur == null )
			cur = new Integer(0);
		
		globalObjectNumbers.put( obj, cur + 1 );
		return obj + cur;
	}
	
	
	
	public Object create( Element node ) throws Exception {
		Map<String,String> params = getAttributes( node );
		Object obj = create( params.get( "class" ), params );
		Map<String,String> realParams = ParameterInjection.extract( obj );
		for( String key : realParams.keySet() )
			node.setAttribute( key, realParams.get( key ) );
		
		if( obj instanceof Configurable ){
			Configurable c = (Configurable) obj;
			c.init( node );
		}

		return obj;
	}
	
	
	public Object create( String className, Map<String,String> parameter ) throws Exception {

		Map<String,String> params = new HashMap<String,String>();
		params.putAll( variables );
		params.putAll( parameter );

		log.debug( "Parameters for new class: {}", params );

		Class<?> clazz = Class.forName( className );

		// create an instance of this class
		//
		Object object = clazz.newInstance();
		
		// Inject the parameters into the object...
		//
		ParameterInjection.inject( object, parameter );
		return object;
	}
	

	public Map<String,String> getAttributes( Node node ){
		Map<String,String> map = new LinkedHashMap<String,String>();
		NamedNodeMap att = node.getAttributes();
		for( int i = 0; i < att.getLength(); i++ ){
			Node attr = att.item(i);
			map.put( attr.getNodeName(), expand( attr.getNodeValue() ) );
		}
		return map;
	}
}
