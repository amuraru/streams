package stream.eval;
/**
 * 
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.mapper.Mapper;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsListener;
import stream.io.DataStreamListener;
import stream.learner.Learner;

/**
 * <p>
 * This class implements a test-and-train strategy for data streams. Elements arriving at
 * instances of this class will be used for testing and after that will be provided to all
 * the registered learners for training.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class TestAndTrain<D,L extends Learner<D,?>> 
implements DataStreamListener
{

	static Logger log = LoggerFactory.getLogger( TestAndTrain.class );

	public final static String EXAMPLES_0_1_SORTED = "/sorted-examples_[0,1].data.gz";
	public final static String EXAMPLES_0_1 = "/examples_[0,1].data.gz";
	public final static String MEMORY_STATISTICS = "__memory_performance__";
	public final static String LEARNER_PERFORMANCE = "__learner_performance__";

	int tests = 0;
	String input = "";
	boolean firstTest = true;
	Integer trainInterval = 1;
	Integer testInterval = 25;
	List<StatisticsListener> performanceListener = new ArrayList<StatisticsListener>();
	List<StatisticsListener> memoryListener = new ArrayList<StatisticsListener>();

	Map<String,Statistics> total = new HashMap<String,Statistics>();

	MemoryUsage memoryUsage = new MemoryUsage();
	Statistics totalError = new Statistics();
	Statistics maxMem = new Statistics();
	AbstractTest<D,Learner<D,?>> test;
	Mapper<Data,D> dataMapper;


	@SuppressWarnings("unchecked")
	public TestAndTrain( Mapper<Data,D> dataMapper, AbstractTest<D,Learner<D,?>> test ){
		this.dataMapper = dataMapper;
		this.test = test;

		if( test == null )
			log.warn( "No evaluator present!" );
		else
			log.debug( "Evaluator for test-and-train is: {}", test.getClass() );

		if( test instanceof Mapper ){
			//
			// TODO: Put this into a preprocessing chain for the test-and-train stuff...
			//
			log.info( "Using implicit data-mapping of class {}", test.getClass() );
			this.dataMapper = (Mapper<Data,D>) test;
		}
	}

	
	public Map<String,ConfusionMatrix<String>> getConfusionMatrices(){
		Map<String,ConfusionMatrix<String>> matrices = new LinkedHashMap<String,ConfusionMatrix<String>>();
		for( String learner : test.getLearnerCollection().keySet() )
			matrices.put( learner, test.getConfusionMatrix( learner ) );
		
		return matrices;
	}
	

	public Statistics test( Data data ){

		if( tests == 0 || tests % this.getTestInterval() != 0 )
			return null;

		log.debug( "Data item count is {}, running evaluation on data: {}", tests, data );

		Statistics error = new Statistics();
		error.put( "Events", new Double( tests ) );
		error.add( test.test( map( data ) ) );



		totalError.add( error );
		totalError.put( "Events", new Double( tests ) );

		for( StatisticsListener l : this.performanceListener ){
			//l.dataArrived( totalError.setName( "Error averaged over #events" ) );
			//l.dataArrived( totalError.divideBy( "Events" ).setName( "Error averaged over #events" ).setKey( "average-model-error" ) );
			l.dataArrived( error.setKey( "absolute-model-error" ) );
			//l.dataArrived( totalError.setName( "Error averaged over #events" ) );
		}

		log.debug( "Error: {}", error );

		Map<String,Object> objs = new LinkedHashMap<String,Object>();
		for( String key : test.getLearnerCollection().keySet() )
			objs.put( key, test.getLearnerCollection().get( key ) );


		Statistics memory = new Statistics();
		memory.put( "Events", new Double( tests ) );

		Statistics curMem = memoryUsage.getGenericUsage( objs );
		maxMem.max( memoryUsage.getGenericUsage( objs ) );
		memory.add( curMem );

		for( StatisticsListener l :  this.memoryListener )
			l.dataArrived( memory );

		return error;
	}

	public void train( Data data ){
		D value = map( data );

		test.getBaselineLearner().learn( value );

		Map<String,Learner<D,?>> learner = test.getLearnerCollection();

		for( String key : learner.keySet() ){
			Learner<D,?> learn = (Learner<D,?>) learner.get( key );
			learn.learn( value );
		}

		tests++;
	}


	/**
	 * @return the dataMapper
	 */
	public Mapper<Data, D> getDataMapper() {
		return dataMapper;
	}

	/**
	 * @param dataMapper the dataMapper to set
	 */
	public void setDataMapper(Mapper<Data, D> dataMapper) {
		this.dataMapper = dataMapper;
	}


	/**
	 * @return the input
	 */
	public String getInput() {
		return input;
	}


	/**
	 * @param input the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}


	public Integer getTrainInterval(){
		return this.trainInterval;
	}

	public void setTestInterval( Integer interval ){
		this.testInterval = interval;
	}

	public Integer getTestInterval(){
		return this.testInterval;
	}


	public void addLearner( String name, Learner<D,?> learner ){
		test.getLearnerCollection().put( name, learner );
	}

	public Map<String,Learner<D,?>> getLearner(){
		return test.getLearnerCollection();
	}


	@SuppressWarnings("unchecked")
	public D map( Data data ){
		try {
			if( dataMapper == null )
				return (D) data;

			return dataMapper.map( data );
		} catch (Exception e) {
			log.error( "Failed to map data-item: {}\n{}", e.getMessage(), e );
			return null;
		}
	}


	public Map<String,String> getProperties(){
		Map<String,String> p = new LinkedHashMap<String,String>();
		p.put( "test.trainInterval", this.getTrainInterval() + "" );
		p.put( "test.testInterval", this.getTestInterval() + "" );
		p.put( "test.baseline.learner", test.getBaselineLearner().toString() );
		for( String key : test.getLearnerCollection().keySet() )
			p.put( "test.learner['" + key + "']", test.getLearnerCollection().get(key).toString() );

		p.putAll( test.getProperties() );
		return p;
	}


	public void testAndTrain( Map<String,Object> datum ){
	}


	public void addPerformanceListener( StatisticsListener l ){
		if( ! performanceListener.contains(l) ) 
			performanceListener.add( l );
	}


	public void addMemoryListener( StatisticsListener l ){
		if( ! memoryListener.contains( l ) )
			memoryListener.add( l );
	}


	/**
	 * @see stream.io.DataStreamListener#dataArrived(java.util.Map)
	 */
	@Override
	public void dataArrived(Data datum) {
		if( datum.isEmpty() )
			return;

		String attribute = datum.keySet().iterator().next();
		Double value = (Double) datum.get( attribute );

		log.debug( "Testing models on data {}={}", attribute, value );
		this.test( datum );

		log.debug( "Training models on data {}={}", attribute, value );
		this.train( datum );
	}

	public void addToTotal( String key, Statistics stats ){
		Statistics st = this.total.get( key );
		if( st == null ){
			st = new Statistics();
			total.put( key, st );
		}

		st.add( stats );
	}


	public List<String> getLearnerNames(){
		return new LinkedList<String>( test.getLearnerCollection().keySet() );
		//return new LinkedList<String>( total.keySet() );
	}


	public String createLearnerTableHtml(){
		StringBuffer s = new StringBuffer();
		s.append( "<table class=\"learnerTable\" align=\"center\">\n" );
		s.append( "<tr><th>Learner</th><th>Settings</th>\n" );
		s.append( "<tr><td><code>Groud Truth</code></td><td><code>" + test.getBaselineLearner().toString() + "</td></code></tr>\n" );

		for( String key : test.getLearnerCollection().keySet() ){
			s.append( "<tr>" );
			s.append( "<td><code>" + key + "</code></td>" );
			s.append( "<td><code>" + test.getLearnerCollection().get( key ).toString().replace( "{", "</code><br/><code> {") + "</code></td>" );
			s.append( "</tr>" );
		}

		s.append( "</table>\n" );
		return s.toString();
	}
}