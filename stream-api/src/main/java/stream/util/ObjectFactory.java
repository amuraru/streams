/**
 * 
 */
package stream.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	static Logger log = LoggerFactory.getLogger( ObjectFactory.class );
	static Map<String,Integer> globalObjectNumbers = new HashMap<String,Integer>();
	final static Map<String,String> classNames = new HashMap<String,String>();
	
	final static String[] DEFAULT_PACKAGES = new String[]{
		"", 
		"stream.data.", 
		"stream.data.mapper.", 
		"stream.data.tree.", 
		"stream.filter.", 
		"stream.data.filter.", 
		"stream.data.stats.", 
		"stream.data.vector.", 
		"stream.data.test.",
		"stream.logic"
	};
	
	final List<String> searchPath = new ArrayList<String>();
	
	
	protected ObjectFactory(){
		super( new HashMap<String,String>() );
		
		for( String pkg : DEFAULT_PACKAGES ){
			searchPath.add( pkg );
		}
	}

	public void addPackage( String pkg ){
		String name = pkg;
		if( ! name.endsWith( "." ) )
			name = name + ".";
		
		if( ! searchPath.contains( name ) )
			searchPath.add( 0, name );
		else
			log.warn( "Package {} already in search-path!", pkg );
	}
	
	
	public List<String> getSearchPaths(){
		return searchPath;
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
		Object obj = create( this.findClassForElement( node ), params );
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
		
		
		//
		// Special case for handling the XML content as __EMBEDDED_CONTENT__
		// parameter (e.g. for the ScriptDataProcessor)
		//
		if( node.getNodeType() == Node.ELEMENT_NODE ){
			Element element = (Element) node;
			String text = element.getTextContent();
			if( text != null && ! "".equals( text.trim() ) ){
 				map.put( EmbeddedContent.KEY, text );
			}
		}
		
		return map;
	}
	
	
	public String findClassForElement( Element node ) throws Exception {
	    if( node.getAttribute( "class" ) != null && ! "".equals( node.getAttribute( "class" ) ) )
	        return node.getAttribute( "class" );
	    
	    String[] prefixes = new String[]{
	            "", "stream.data.", "stream.data.mapper.", "stream.data.tree.", "stream.filter.", "stream.data.filter.", "stream.data.stats.", "stream.data.vector.", "stream.data.test." 
	    };
	    
	    
	    for( String prefix : prefixes ){
	        
	        try {
	            Class<?> clazz = Class.forName( prefix + node.getNodeName() );
	            log.debug( "Auto-detected class {} for node {}", clazz, node.getNodeName() );
	            return clazz.getName();
	        } catch (Exception e) {
	            log.debug( "No class '{}' found", prefix + node.getNodeName() );
	        }
	    }
	    
	    throw new Exception( "Failed to determine class for node '" + node.getNodeName() + "'!");
	}
}
