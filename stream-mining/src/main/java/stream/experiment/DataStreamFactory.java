/**
 * 
 */
package stream.experiment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.data.DataProcessor;
import stream.io.DataSource;
import stream.io.DataStreamProcessor;
import stream.util.ObjectFactory;
import stream.util.ParameterInjection;

/**
 * @author chris
 *
 */
public class DataStreamFactory
	extends VariableContext
{
	static Logger log = LoggerFactory.getLogger( DataStreamFactory.class );
	final static Map<String,Class<?>> PROCESSORS = new HashMap<String,Class<?>>();
	static {
		PROCESSORS.put( "AttributeFilter".toLowerCase(), stream.data.mapper.FeatureNameFilter.class );
	}
	
	public static DataStreamFactory newInstance(){
		return new DataStreamFactory( new HashMap<String,String>() );
	}
	
	
	public static DataStreamFactory newInstance( Map<String,String> map ){
		if( map == null )
			return newInstance();
		return new DataStreamFactory( map );
	}
	
	
	private DataStreamFactory( Map<String,String> context ){
		super( context );
	}
	

	
	
	
	public DataProcessor createDataProcessor( Element el ) throws Exception {

		if( el != null && el.getNodeName().equalsIgnoreCase( "processor" ) ){
			
			Map<String,String> config = getAttributes( el );
			if( config.get( "class" ) == null )
				throw new Exception( "No class specified for DataProcessor element!" );
			
			Class<?> clazz = Class.forName( config.get( "class" ) );
			DataProcessor processor = (DataProcessor) clazz.newInstance();
			ParameterInjection.inject( processor, config );
			log.info( "Created new DataProcessor {}  from config: {}", processor, config );
			return processor;
		}

		return null;
	}


	
	public DataStreamProcessor createDataStreamProcessor( Element el ) throws Exception {
		
		if( el != null && el.getNodeName().equalsIgnoreCase( "processing" ) ){
			
			Map<String,String> config = getAttributes( el );
			if( config.get( "class" ) == null ){
				config.put( "class", "stream.io.DataStreamProcessor" );
			}
			
			Class<?> clazz = Class.forName( config.get( "class" ) );
			DataStreamProcessor stream = (DataStreamProcessor) clazz.newInstance();
			ParameterInjection.inject( stream, config );
			
			NodeList children = el.getChildNodes();
			for( int i = 0; i < children.getLength(); i++ ){
				Node node = children.item(i);
				if( node.getNodeType() == Node.ELEMENT_NODE ){
					DataProcessor processor = createDataProcessor( (Element) node );
					if( processor != null )
						stream.addDataProcessor( processor );
				}
			}
			
			return stream;
		}
		
		return null;
	}


	public DataSource createDataSource( Element el ) throws Exception {

		/*
		String className = el.getAttribute( "class" );
		if( className != null && className.equals( "stream.generator.LabeledGaussianStream" ) ){
			LabeledGaussianStream ds = (LabeledGaussianStream) ObjectFactory.newInstance().create( el );
			return ds;
		}
		 */
		
		Map<String,String> ds = getAttributes( el );
		DataSource d = new DataSource( ds.get("name"), ds.get("url"), ds.get( "class" ) );
		if( ds.get( "descriptionUrl" ) != null )
			d.setDescriptionRef( ds.get( "descriptionUrl" ) );
		
		Map<String,String> params = ObjectFactory.newInstance().getAttributes( el );
		for( String k : params.keySet() ){
			d.setParameter( k, params.get( k ) );
		}
		return d;
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