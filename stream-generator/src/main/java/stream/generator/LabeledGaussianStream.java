/**
 * 
 */
package stream.generator;

import java.io.File;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.stats.Statistics;
import stream.io.DataStream;
import stream.util.ObjectFactory;
import stream.util.ParameterInjection;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * @author chris
 *
 */
public class LabeledGaussianStream 
	extends GeneratorDataStream 
{
	static Logger log = LoggerFactory.getLogger( LabeledGaussianStream.class );

	Map<String,Map<String,Gaussian>> generators = new LinkedHashMap<String,Map<String,Gaussian>>();
	List<String> labels = new ArrayList<String>();
	Map<String,Double> classWeights = new LinkedHashMap<String,Double>();
	
	/* The data types provided by this stream */
	Map<String,Class<?>> types = new LinkedHashMap<String,Class<?>>();

	/* The random generator to choose which class to create next */
	Random random = new Random();

	Long seed = System.currentTimeMillis();

	Random seedGenerator = new Random();
	
	ProportionalOracle classOracle = new ProportionalOracle();

	Statistics classCounts = new Statistics();
	
	Map<String,DenseDoubleMatrix2D> covMatrices = new LinkedHashMap<String,DenseDoubleMatrix2D>();
	
	
	public ProportionalOracle getClassOracle(){
		return classOracle;
	}
	
	
	public Gaussian getGaussian( String label, String attribute ){
		return generators.get( label ).get( attribute );
	}
	
	
	/**
	 * Create a new Gaussian stream with the given number of classes. The map contains a mapping
	 * of attribute-names to double-pairs each of which describes the mean/variance of a Gaussian
	 * distribution.  
	 * 
	 * @param numberOfClasses  The number of classes.
	 * @param attributeDistributions  The parameterization of the attribute distributions.
	 */
	public LabeledGaussianStream( int numberOfClasses ){
		labels.clear();
		labels.add( "positive" );
		labels.add( "negative" );

		if( numberOfClasses > 2 ){
			labels.clear();
			for( int i = 0; i < numberOfClasses; i++ )
				labels.add( "class" + i );
		}
	}


	public LabeledGaussianStream( String[] classNames ){
		labels.clear();
		for( String clazz : classNames )
			labels.add( clazz );
	}


	public LabeledGaussianStream( Element node ) throws Exception {
		log.debug( "Creating LabeledGaussianStream from DOM node..." );

		Map<String,String> attr = ObjectFactory.newInstance().getAttributes( node );
		ParameterInjection.inject( this, attr );
		this.init( node );
	}


	public LabeledGaussianStream(){
	}


	public void init( Node n ) throws Exception {

		log.debug( "Using seed: '{}'", this.getSeed() );
		Element node;

		if( n.getNodeType() == Node.ELEMENT_NODE )
			node = (Element) n;
		else
			return;

		Double totalWeights = 0.0d;
		NodeList classes = node.getElementsByTagName( "Class");
		for( int i = 0; i < classes.getLength(); i++ ){

			Node cl = classes.item(i);
			Element clazz = (Element) cl;
			String name = clazz.getAttribute( "name" );
			if( name == null || name.isEmpty() )
				throw new Exception( "'name' attribute is required for class-node!" );

			
			String weight = clazz.getAttribute( "weight" );
			if( weight == null || weight.trim().isEmpty() )
				weight = "1.0";
			
			try {
				Double w = new Double( weight );
				classWeights.put( name, w );
				totalWeights += w;
			} catch (Exception e) {
				log.error( "Invalid class-weight '{}' for class '{}'!", weight, name );
				throw e;
			}
			
			this.labels.add( name );
			this.parseGenerators( name, clazz );
		}

		log.debug( "Adjusting final class-weights: {}", classWeights );
		classOracle.setSeed( getNextSeed() );
		classOracle.setWeights( classWeights );
		log.debug( "classOracle: {}", classOracle );
	}


	private void parseGenerators( String label, Element node ) throws Exception {

		Map<String,Gaussian> generators = new LinkedHashMap<String,Gaussian>();

		NodeList list = node.getChildNodes();
		for( int i = 0; i < list.getLength(); i++ ){
			Node ch = list.item(i);
			if( ch.getNodeType() == Node.ELEMENT_NODE && ch.getNodeName().equalsIgnoreCase( "Attribute" ) ){

				Map<String,String> params = ObjectFactory.newInstance().getAttributes( ch );
				String name = params.get( "name" );
				if( name == null || name.isEmpty() )
					throw new Exception( "Missing 'name' attribute for node '" + ch.getNodeName() + "'!" );

				String className = params.get( "class" );
				if( className == null || className.isEmpty() )
					throw new Exception( "Missing 'class' attribute for node '" + ch.getNodeName() + "'!" );


				if( !params.containsKey( "seed" ) ){
					params.put( "seed", "" + this.seedGenerator.nextLong() );
					log.debug( "Setting unspecified seed for generator '{}' to '{}'", name, params.get( "seed" ) );
				}

				Class<?> clazz = Class.forName( className );
				Gaussian g = (Gaussian) clazz.newInstance();
				ParameterInjection.inject( g, params );
				generators.put( name, g );
				this.types.put( name, Double.class );
			}
		}

		this.generators.put( label, generators );
	}


	/**
	 * @return the seed
	 */
	public Long getSeed() {
		return seed;
	}


	/**
	 * @param seed the seed to set
	 */
	public void setSeed(Long seed) {
		this.seed = seed;
		random = new Random( this.seed );
		this.seedGenerator = new Random( random.nextLong() );
	}


	public Data generate(){
		String label = classOracle.getNext();
		this.classCounts.add( label, 1.0d );
		return generate( label );
	}


	protected Data generate( String label ){
		Data item = new DataImpl();

		Map<String,Gaussian> gen = generators.get( label );
		for( String attribute : gen.keySet() ){
			Gaussian g = gen.get( attribute );
			item.put( attribute, g.next() );
		}

		item.put( "label", label );
		return item;
	}
	
	


	public void setGenerator( int clazz, String attribute, Gaussian dist ){
		this.setGenerator( labels.get( clazz % labels.size() ), attribute, dist );
	}


	public void setGenerator( String label, String attribute, Gaussian dist ){

		if( !labels.contains( label ) )
			labels.add( label );

		Map<String,Gaussian> attributes = this.generators.get( label );
		if( attributes == null ){
			attributes = new LinkedHashMap<String,Gaussian>();
			generators.put( label, attributes );
		}
		attributes.put( attribute, dist );
		if( !this.types.containsKey( attribute ) )
			types.put( attribute, Double.class );

		if( dist.getSeed() == null ){
			Long seed = getNextSeed();
			log.info( "Setting seed for new generator to {}", seed );
			dist.setSeed( seed );
		}
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
	 * @see stream.io.AbstractDataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data item) throws Exception {
		//
		// TODO: This is cheated: we still create a new item-object, which should not
		//       happen... :-)
		//
		Data gen = this.generate();
		item.clear();
		item.putAll( gen );
		return item;
	}


	public String getDescription(){
		StringBuffer s = new StringBuffer();
		s.append( "<p>" );
		s.append( "This is a pseudo-randomized generated data stream, consisting of " + types.size() + " attributes. <br/>" );
		s.append( "The attributes are { " + this.types.keySet() + " } and are independently generated using the " );
		s.append( "following generators:" );
		s.append( "<table>" );
		for( String att : types.keySet() ){
			s.append( "<tr>" );
			s.append( "<td>" );
			s.append( att );
			s.append( "</td>" );
			s.append( "<td>" );
			for( String label : labels ){
				Map<String,Gaussian> gen = generators.get( label );
				Gaussian g = gen.get( att );
				s.append( g.toHtml() );
			}
			s.append( "</td>" );
			s.append( "</tr>" );
		}
		s.append( "</table>\n");
		s.append( "</p>" );
		s.append( "<p>" );
		s.append( "The data elements are labeled with " + labels.size() + " classes: { " );
		Iterator<String> it = labels.iterator();
		while( it.hasNext() ){
			String l = it.next();
			s.append( l );
			if( it.hasNext() )
				s.append( ", " );
		}
		s.append( " }. <br/>" );
		s.append( "The proportions of the classes are:" );
		s.append( "<table><tr><td>Class</td><td>Proportion</td></tr>\n" );
		DecimalFormat fmt = new DecimalFormat( "0.000%" );
		for( String label : classOracle.getLabels() ){
			s.append( "<tr>" );
			s.append( "<td>" + label + "</td>" );
			s.append( "<td>" + fmt.format(classOracle.getWeight( label )) + "</td>" );
			s.append( "</tr>" );
		}
		s.append( "</table>" );

		s.append( "<ul>" );
		for( String key : classCounts.keySet() ){
			s.append( "<li>Class <i>" + key + "</i> has " + classCounts.get( key ) + " instances.</li>" );
		}
		s.append( "</ul>" );
		s.append( "</p>" );


		return s.toString();
	}


	public Long getNextSeed(){
		return seedGenerator.nextLong();
	}

	/**
	 * @see stream.experiment.DataSource#createDataStream()
	 */
	//@Override
	public DataStream createDataStream() throws Exception {
		return this;
	}


	/**
	 * @see stream.experiment.DataSource#getClassName()
	 */
	//@Override
	public String getClassName() {
		return getClass().getName();
	}

	public void setClassName( String cl ){
	}


	
	public static LabeledGaussianStream createFromFile( File file ) throws Exception {
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = b.parse( file );
		LabeledGaussianStream g = new LabeledGaussianStream( doc.getDocumentElement() );
		return g;
	}

	
	public static LabeledGaussianStream createFromXML( String xml ) throws Exception {
		DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = b.parse( new InputSource( new StringReader( xml ) ) );
		LabeledGaussianStream g = new LabeledGaussianStream( doc.getDocumentElement() );
		return g;
	}


	/**
	 * @see stream.io.DataStream#close()
	 */
	@Override
	public void close() {
	}
}