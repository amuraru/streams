/**
 * 
 */
package stream.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.data.Data;
import stream.eval.AbstractTest;
import stream.eval.TestAndTrain;
import stream.io.DataSource;
import stream.io.DataStream;
import stream.learner.Learner;
import stream.util.ObjectFactory;
import stream.util.ParameterInjection;

/**
 * @author chris
 *
 */
public class ExperimentFactory {
	static Logger log = LoggerFactory.getLogger( ExperimentFactory.class );
	static Map<String,String> globalSettings = new HashMap<String,String>();
	static Map<String,Integer> globalObjectNumbers = new HashMap<String,Integer>();
	
	public static synchronized String getNextIdentifier( String obj ){
		Integer cur = globalObjectNumbers.get( obj );
		if( cur == null )
			cur = new Integer(0);
		
		globalObjectNumbers.put( obj, cur + 1 );
		return obj + cur;
	}

	public static Learner<?,?> create( String className, Map<String,String> parameter ) throws Exception {

		Map<String,String> params = new HashMap<String,String>();
		params.putAll( globalSettings );
		params.putAll( parameter );

		//log.info( "Parameters for new learner: {}", params );

		Class<?> clazz = Class.forName( className );
		if( !Learner.class.isAssignableFrom( clazz ) )
			throw new Exception( "Class '" + className + "' does not implement the Learner interface!" );

		// create an instance of this class
		//
		Object learner = clazz.newInstance();

		// Inject the parameters into the learner...
		//
		ParameterInjection.inject( learner, parameter );
		return (Learner<?,?>) learner;
	}


	public static Map<String,Map<String,String>> findLearnerConfigs( URL url ) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse( url.openStream() );
		findGlobalSettings( doc.getDocumentElement() );
		return findLearner( doc.getDocumentElement() );
	}

	public static Experiment parseExperiment( URL url ) throws Exception {
		return null;
	}

	public static Experiment parseExperiment( File file ) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse( new FileInputStream( file ) );
		findGlobalSettings( doc.getDocumentElement() );


		String out = doc.getDocumentElement().getAttribute( "output" );
		log.info( "output-attribute is: {}", out );
		if( out == null )
			out = "output";

		Experiment e = new Experiment( file.getName(), new File( out ) );
		e.setExperimentFile( file );
		for( String key : globalSettings.keySet() ){
			e.set( key, globalSettings.get( key ) );
		}

		try {
			e.setRange( 0, new Integer( globalSettings.get( "limit" ) ) );
		} catch (Exception ex) {
			log.info( "Using default range 0-10000" );
			e.setRange( 0, 10000 );
		}

		log.info( "Using output-directory {}", e.getOutputDirectory() );
		log.info( "globalSettings: {}", globalSettings );

		Map<String,Map<String,String>> streams = findElement( "stream", doc.getDocumentElement() );
		if( !streams.isEmpty() ){
			Map<String,String> map = streams.values().iterator().next();
			DataSource ds = new DataSource( map.get( "name" ) + "", map.get( "url" ), map.get( "class" ) );
			e.setDataSource( ds );
		}

		Map<String,Learner<?,?>> learner = ExperimentFactory.createLearner( doc );
		for( String key : learner.keySet() ){
			e.setLearner( key, learner.get(key) );
		}

		return e;
	}


	public static Map<String,String> findGlobalSettings( Node node ) throws Exception {
		Map<String,String> found = new LinkedHashMap<String,String>();

		if( node.getNodeName().equalsIgnoreCase( "experiment" ) && (node.getParentNode() == null || node.getParentNode().getNodeType() == Node.DOCUMENT_NODE ) ){
			NamedNodeMap attributes = node.getAttributes();
			for( int i = 0; i < attributes.getLength(); i++ ){
				Node attribute = attributes.item(i);
				if( attribute.getNodeType() == Node.ATTRIBUTE_NODE ){
					log.info( "Adding experiment property {} = {}", attribute.getNodeName(), attribute.getNodeValue() );
					globalSettings.put( attribute.getNodeName(), attribute.getNodeValue() );
				}
			}
		}

		if( node.getNodeName().equalsIgnoreCase( "property" ) ){


			String name = null;
			String value = null;
			NamedNodeMap attributes = node.getAttributes();
			for( int i = 0; i < attributes.getLength(); i++ ){
				Node attribute = attributes.item( i );
				if( attribute.getNodeName().equals( "name" ) )
					name = attribute.getNodeValue();

				if( attribute.getNodeName().equals( "value" ) )
					value = attribute.getNodeValue();
			}

			if( name != null && value != null ){
				//log.info( "Found global setting: {} = {}", name, value );
				globalSettings.put( name, value );
			}

		} else {
			NodeList list = node.getChildNodes();
			for( int i = 0; i < list.getLength(); i++ ){
				findGlobalSettings( list.item(i) );
			}
		}

		found.putAll( globalSettings );
		return found;
	}



	public static Map<String,Map<String,String>> findElements( String element, URL url ) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse( url.openStream() );
		return findElement( element, doc.getDocumentElement() );
	}


	public static Map<String,DataSource> findDataSources( Node node ) throws Exception {
		Map<String,Map<String,String>> ds = findElement( "stream", node );
		Map<String,DataSource> sources = new LinkedHashMap<String,DataSource>();
		for( String key : ds.keySet() ){
			DataSource d = new DataSource( key, ds.get(key).get("url"), ds.get(key).get( "class" ) );
			Map<String,String> params = ObjectFactory.newInstance().getAttributes( node );
			for( String pk : params.keySet() ){
				d.setParameter( pk, params.get( pk ) );
			}
			sources.put( key, d );
		}
		return sources;
	}


	public static List<Element> findDataSourceNodes( Document doc ) throws Exception {
		List<Element> result = new LinkedList<Element>();
		NodeList list = doc.getElementsByTagName( "Stream" );
		for( int i = 0; i < list.getLength(); i++ )
			result.add( (Element) list.item(i) );
		return result;
	}

	public static List<Element> findLearnerNodes( Node node ) throws Exception {
		List<Element> result = new LinkedList<Element>();

		if( node.getNodeName().equalsIgnoreCase("learner") && node.getNodeType() == Node.ELEMENT_NODE ){
			result.add( (Element) node );
		} else {
			NodeList nl = node.getChildNodes();
			for( int i = 0; i < nl.getLength(); i++ ){
				result.addAll( findLearnerNodes( nl.item(i) ) );
			}
		}

		return result;
	}

	public static Map<String,Map<String,String>> findLearner( Node node ) throws Exception {
		Map<String,Map<String,String>> found = new LinkedHashMap<String,Map<String,String>>();

		if( node.getNodeName().equalsIgnoreCase( "learner" ) ){

			String name = null;
			Map<String,String> config = new HashMap<String,String>();
			NamedNodeMap attributes = node.getAttributes();
			for( int i = 0; i < attributes.getLength(); i++ ){
				Node attribute = attributes.item( i );
				config.put( attribute.getNodeName(), attribute.getNodeValue() );
				if( attribute.getNodeName().equals( "name" ) )
					name = attribute.getNodeValue();
			}

			if( name == null )
				throw new Exception( "Missing 'name' attribute for learner node!" );

			found.put( name, config );

		} else {
			NodeList list = node.getChildNodes();
			for( int i = 0; i < list.getLength(); i++ ){
				Map<String,Map<String,String>> cfg = findLearner( list.item(i) );
				if( cfg != null )
					found.putAll( cfg );
			}
		}

		return found;
	}


	public static Map<String,Learner<?,?>> createLearner( URL url ) throws Exception {
		Map<String,Learner<?,?>> result = new HashMap<String,Learner<?,?>>();

		Map<String,Map<String,String>> configs = ExperimentFactory.findLearnerConfigs( url );
		for( String key : configs.keySet() ){
			Map<String,String> parameters = configs.get( key );
			log.info( "Learner '{}' has config: {}", key, configs.get( key ) );
			//log.info( "   class of Learner is '{}'", parameters.get( "class" ) );
			Learner<?,?> l = ExperimentFactory.create( parameters.get( "class" ), parameters );
			log.info( "   Created learner: {}", l );
			result.put( key, l );
		}

		return result;
	}


	public static Map<String,Learner<?,?>> createLearner( Document doc ) throws Exception {
		Map<String,Learner<?,?>> result = new HashMap<String,Learner<?,?>>();

		Map<String,Map<String,String>> configs = ExperimentFactory.findLearner( doc.getDocumentElement() );
		for( String key : configs.keySet() ){
			Map<String,String> parameters = configs.get( key );
			log.info( "Learner '{}' has config: {}", key, configs.get( key ) );
			//log.info( "   class of Learner is '{}'", parameters.get( "class" ) );
			if( parameters.get( "class" ) != null && result.containsKey( key ) ){
				log.error( "Multiple learners with name '{}' defined!" );
				throw new Exception( "Multiple learners defined with the same name '" + key + "'!" );
			}
			Learner<?,?> l = ExperimentFactory.create( parameters.get( "class" ), parameters );
			log.info( "   Created learner: {}", l );
			result.put( key, l );
		}

		return result;
	}


	public static Learner<?,?> createLearner( Node node ) throws Exception {

		if( node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase( "learner" ) ){
			log.info( "Creating learner from node... " );
			Element element = (Element) node;
			String name = null;
			Map<String,String> config = new HashMap<String,String>( globalSettings );
			NamedNodeMap attributes = node.getAttributes();
			for( int i = 0; i < attributes.getLength(); i++ ){
				Node attribute = attributes.item( i );
				config.put( attribute.getNodeName(), attribute.getNodeValue() );
				if( attribute.getNodeName().equals( "name" ) )
					name = attribute.getNodeValue();
			}

			if( name == null )
				name = ExperimentFactory.getNextIdentifier( node.getNodeName() );

			Learner<?,?> learner = ExperimentFactory.create( config.get( "class" ), config );
			Map<String,String> params = ParameterInjection.extract( learner );
			log.info( "   adding final parameters back to node: {}", params );
			for( String key : params.keySet() )
				element.setAttribute( key, params.get( key ) );

			return learner;
		}

		return null;
	}


	public static Map<String,Map<String,String>> findElement( String elementName, Node node ) throws Exception {
		Map<String,Map<String,String>> found = new LinkedHashMap<String,Map<String,String>>();

		if( node.getNodeName().equalsIgnoreCase( elementName ) ){

			String name = null;
			Map<String,String> config = new HashMap<String,String>();
			NamedNodeMap attributes = node.getAttributes();
			for( int i = 0; i < attributes.getLength(); i++ ){
				Node attribute = attributes.item( i );
				config.put( attribute.getNodeName(), attribute.getNodeValue() );
				if( attribute.getNodeName().equals( "name" ) )
					name = attribute.getNodeValue();
			}

			if( name == null )
				name = ExperimentFactory.getNextIdentifier( node.getNodeName() );

			found.put( name, config );

		} else {
			NodeList list = node.getChildNodes();
			for( int i = 0; i < list.getLength(); i++ ){
				Map<String,Map<String,String>> cfg = findElement( elementName, list.item(i) );
				if( cfg != null )
					found.putAll( cfg );
			}
		}

		return found;
	}


	public static DataStream createStream( Map<String,String> params ) throws Exception {
		Class<?> clazz = Class.forName( params.get( "class" ) );
		Constructor<?> constr = clazz.getConstructor( URL.class );
		URL url = new URL(params.get("url") );
		DataStream stream = (DataStream) constr.newInstance( url );
		return stream;
	}


	public static void main(String[] args) throws Exception {

		//String className = "stream.quantiles.GKQuantiles";
		//Map<String,String> params = new HashMap<String,String>();
		//params.put( "epsilon", "0.05" );
		//LearnerFactory.create( className, params );



		URL url = ExperimentFactory.class.getResource( "/experiment.xml" );
		Map<String,Map<String,String>> streams = ExperimentFactory.findElements( "stream", url );
		for( String key : streams.keySet() ){
			Map<String,String> stream = streams.get( key );
			log.info( "Experiment contains stream '{}': {}", key, stream );

			DataStream ds = createStream( stream );
			Map<String,Serializable> datum = ds.readNext();
			log.info( "First datum is {}", datum );
		}

		Map<String,Map<String,String>> configs = ExperimentFactory.findLearnerConfigs( url );
		for( String key : configs.keySet() ){
			Map<String,String> parameters = configs.get( key );
			log.info( "Learner '{}' has config: {}", key, configs.get( key ) );
			//log.info( "   class of Learner is '{}'", parameters.get( "class" ) );
			Learner<?,?> l = ExperimentFactory.create( parameters.get( "class" ), parameters );
			log.info( "   Created learner: {}", l );

			Map<String,String> p = ParameterInjection.extract( l );
			log.info( "Extracted parameter: {}", p );
		}
	}

	public static Map<String,String> getAttributes( Node node ){
		Map<String,String> map = new LinkedHashMap<String,String>();
		NamedNodeMap att = node.getAttributes();
		for( int i = 0; i < att.getLength(); i++ ){
			Node attr = att.item(i);
			map.put( attr.getNodeName(), attr.getNodeValue() );
		}
		return map;
	}

	public static Map<String,DataStream> createDataStreams( Document doc ) throws Exception {
		Map<String,DataStream> ds = new LinkedHashMap<String,DataStream>();

		Map<String,Map<String,String>> streams = ExperimentFactory.findElement( "stream", doc.getDocumentElement() );
		for( String key : streams.keySet() ){
			DataStream stream = createStream( streams.get( key ) );
			ds.put( key, stream );
		}
		return ds;
	}

	@SuppressWarnings("unchecked")
	public static TestAndTrain<Data,Learner<Data,?>> findEvaluation( Document doc ) throws Exception {

		ExperimentFactory.findGlobalSettings( doc.getDocumentElement() );

		NodeList list = doc.getElementsByTagName( "TestAndTrain" );
		if( list.getLength() == 0 )
			throw new Exception( "No test-and-train configuration found!" );

		Node t3 = list.item( 0 );
		Map<String,String> evaluationAttributes = getAttributes( t3 );
		if( evaluationAttributes.get( "class" ) == null )
			evaluationAttributes.put( "class", "stream.eval.GenericLearnerTest" );

		
		//
		// check for a baseline learner and create it
		//
		boolean baselineProvided = true;
		Learner<?,?> baselineLearner = null;
		Map<String,String> baselineConfig = new LinkedHashMap<String,String>();
		Map<String,Map<String,String>> baseConfig = ExperimentFactory.findElement( "Baseline", t3 );
		if( baseConfig.isEmpty() || baseConfig.size() > 1 ){
			baselineProvided = false;
			baselineConfig.put( "class", "stream.learner.LabelPredictor" );
			log.info( "No baseline learner found, using preparing default: {}", baselineConfig );
		} else {
			baselineConfig = baseConfig.values().iterator().next();
		}
		if( baselineConfig.get( "name" ) == null )
			baselineConfig.put( "name", getNextIdentifier( "Baseline" ) );
		baselineLearner = ExperimentFactory.create( baselineConfig.get( "class" ), baselineConfig );
		log.info( "Created baselineLearner: {}", baselineLearner );
		
		list = t3.getChildNodes();

		Map<String,Learner<?,?>> learner = new LinkedHashMap<String,Learner<?,?>>();

		List<Element> learnerNodes = ExperimentFactory.findLearnerNodes( t3 );
		for( Element e: learnerNodes ){
			String name = e.getAttribute( "name" );
			//throw new Exception( "Missing attribute 'name' for learner node!" );

			Learner<?,?> learnerInstance = ExperimentFactory.createLearner( e );
			
			if( name == null || name.trim().isEmpty() ){
				name = getNextIdentifier( learnerInstance.getClass().getSimpleName() );
				e.setAttribute( "name", name );
			}
			
			log.info( "Registering learner '{}': {}", name, learnerInstance );
			learner.put( name, learnerInstance );

			Map<String,String> conf = ParameterInjection.extract( learnerInstance );
			for( String key : conf.keySet() )
				e.setAttribute( key, conf.get( key ) );
		}

		log.info( "Creating evaluator {}", evaluationAttributes.get( "class" ) );
		Class<?> evalClass = Class.forName( evaluationAttributes.get( "class" ) );

		Constructor<?> con = evalClass.getConstructor();
		AbstractTest<?,Learner<?,?>> test = (AbstractTest<?,Learner<?,?>>) con.newInstance();
		
		if( test.getBaselineLearner() == null || baselineProvided ){
			log.info( "Setting baselineLearner, (provided: {})", baselineProvided );
			test.setBaselineLearner( baselineLearner );
		} else
			log.info( "Using default baseline-learner" );
		test.getLearnerCollection().putAll( learner );
		
		
		TestAndTrain testAndTrain = new TestAndTrain( null, test );
		ParameterInjection.inject( testAndTrain, evaluationAttributes );
		return testAndTrain;
	}
}
