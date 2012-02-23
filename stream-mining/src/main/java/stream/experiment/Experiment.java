/**
 * 
 */
package stream.experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.plotter.AbstractPlotter;
import stream.data.plotter.StreamPlotter;
import stream.data.stats.MultiStatisticsWriter;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsStreamWriter;
import stream.data.stats.StreamSummarizer;
import stream.data.stats.WindowedStatisticsListener;
import stream.eval.AbstractTest;
import stream.eval.HtmlResult;
import stream.eval.MemoryMonitor;
import stream.eval.PredictionResults;
import stream.eval.TestAndTrain;
import stream.io.DataSource;
import stream.io.DataStream;
import stream.io.DataStreamListener;
import stream.io.DataStreamProcessor;
import stream.learner.Learner;
import stream.util.ParameterInjection;

/**
 * @author chris
 *
 */
public class Experiment
{
	static Logger log = LoggerFactory.getLogger( Experiment.class );

	String name = "";
	Integer start = 0;
	Integer limit = Integer.MAX_VALUE;
	Boolean updatePlots = false;
	Boolean gnuplot = true;
	File experimentFile;
	File outputDirectory;
	Map<String,String> params = new TreeMap<String,String>();
	DataSource dataSource;
	DataStream stream = null;
	Learner<?,?> baseline;
	Integer plotInterval = 5;
	Map<String,Learner<?,?>> learner = new LinkedHashMap<String,Learner<?,?>>();
	List<AbstractPlotter> plots = new LinkedList<AbstractPlotter>();
	TestAndTrain<Data,Learner<Data,?>> evaluation;
	Map<String,DataSource> dataSources = new LinkedHashMap<String,DataSource>();
	List<DataStreamListener> preEvaluation = new LinkedList<DataStreamListener>();
	DataStreamProcessor processor = null; 

	String htmlOutput = "";
	Map<String,HtmlResult> outputElements = new LinkedHashMap<String,HtmlResult>();

	public Experiment( String name, File output ){
		this.name = name;
		this.outputDirectory = output;
	}


	public Experiment( File file ) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse( file );
		this.experimentFile = file;
		init( doc );
	}

	public Experiment( URL url ) throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse( url.openStream() );
		init( doc );
	}


	protected void init( Document doc ) throws Exception {
		Node node = doc.getElementsByTagName( "experiment" ).item(0);
		Map<String,String> attributes = ExperimentFactory.getAttributes( node );
		this.outputDirectory = new File( attributes.get( "output" ) );
		this.outputDirectory.mkdirs();

		this.experimentFile = new File( outputDirectory.getAbsolutePath() + File.separator + "experiment.xml" );
		Map<String,String> settings = ExperimentFactory.findGlobalSettings( doc.getDocumentElement() );
		this.params = new LinkedHashMap<String,String>(ExperimentFactory.globalSettings);

		ParameterInjection.inject( this, settings );

		evaluation = ExperimentFactory.findEvaluation( doc );
		log.info( "Found evaluation: {}", evaluation );

		List<Element> dataSourceElements = ExperimentFactory.findDataSourceNodes(doc);
		DataStreamFactory dsf = DataStreamFactory.newInstance( settings );
		if( dataSourceElements.isEmpty() )
			throw new Exception( "No dataSource defined!" );

		for( Element dse : dataSourceElements ){
			DataSource ds = dsf.createDataSource( dse );
			if( dataSource == null )
				dataSource = ds;

			dataSources.put( ds.getName(), ds );
		}
		//dataSource = dsf.createDataSource( dataSourceElements.get(0) );

		NodeList processorChains = doc.getElementsByTagName( "processing" );
		for( int i = 0; i < processorChains.getLength(); i++ ){
			Element el = (Element) processorChains.item(i);
			processor = dsf.createDataStreamProcessor( el );
		}

		if( processor != null ){
			log.info( "Opening data stream {}", dataSource );
			stream = dataSource.createDataStream();
			processor.setSource( stream );
			log.info( "Replacing data stream by processor-chain: {}" , processor );
			stream = processor;
		}

		try {
			String dsDescription = null; //dataSource.retrieveDescription( ".en.xml");
			if( dsDescription == null || dsDescription.isEmpty() ){
				log.info( "No DataSource description found." );
			}
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setValidating( false );
				Document xmlDesc = dbf.newDocumentBuilder().parse( dataSource.getUrl() + ".en.xml" );
				log.info( "Imported description from XML remote source" );
				dataSourceElements.get(0).appendChild( doc.importNode( xmlDesc.getDocumentElement(), true ) );
			} catch (Exception e) {
				//log.info( "DataSource description:\n{}\n", dsDescription );
				//dataSources.get(0).setTextContent( dsDescription.replace( "<description>", "" ).replace( "</description>", "" ) );
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Transformer tf = TransformerFactory.newInstance().newTransformer();
		DOMSource dom = new DOMSource( doc );
		StreamResult result = new StreamResult( this.experimentFile );
		tf.transform( dom, result );


		tf = TransformerFactory.newInstance().newTransformer( new StreamSource( Experiment.class.getResourceAsStream( "/templates/experiment-report.xsl" ) ) );
		result = new StreamResult( new FileOutputStream( new File( this.getOutputDirectory().getAbsolutePath() + File.separator + "index.html" ) ) );
		tf.transform( dom, result);

		StringWriter html = new StringWriter();
		result = new StreamResult( html );
		tf.transform( dom, result );

		htmlOutput = html.toString();
		ReportProcessor.copyResource( "/templates/style.css", this.getOutputDirectory() );

		this.init();
	}


	/**
	 * @return the gnuplot
	 */
	public Boolean getGnuplot() {
		return gnuplot;
	}


	/**
	 * @param gnuplot the gnuplot to set
	 */
	public void setGnuplot(Boolean gnuplot) {
		this.gnuplot = gnuplot;
	}


	/**
	 * @return the experimentFile
	 */
	public File getExperimentFile() {
		return experimentFile;
	}



	/**
	 * @param experimentFile the experimentFile to set
	 */
	public void setExperimentFile(File experimentFile) {
		this.experimentFile = experimentFile;
	}



	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}


	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	/**
	 * @return the outputDirectory
	 */
	public File getOutputDirectory() {
		return outputDirectory;
	}


	public void setRange( Integer start, Integer end ){
		if( end < start )
			throw new RuntimeException( "Invalid range!" );

		this.start = start;
		this.limit = end;
	}



	public void setParameter( String key, String value ){
		params.put( key, value );
	}

	public String getParameter( String key ){
		return params.get( key );
	}

	public Map<String,String> getParameters(){
		return params;
	}

	public void setLearner( String key, Learner<?,?> l ){
		this.learner.put( key, l );
	}

	public void set( String key, String val ){

	}

	public String get( String key ){
		return key;
	}





	/**
	 * @return the limit
	 */
	public Integer getLimit() {
		return limit;
	}


	/**
	 * @param limit the limit to set
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}


	/**
	 * @return the updatePlots
	 */
	public Boolean getUpdatePlots() {
		return updatePlots;
	}


	/**
	 * @param updatePlots the updatePlots to set
	 */
	public void setUpdatePlots(Boolean updatePlots) {
		this.updatePlots = updatePlots;
	}


	/**
	 * @return the plotInterval
	 */
	public Integer getPlotInterval() {
		return plotInterval;
	}


	/**
	 * @param plotInterval the plotInterval to set
	 */
	public void setPlotInterval(Integer plotInterval) {
		this.plotInterval = plotInterval;
	}


	public void init() throws Exception {

		log.info( "Creating output directory {}", outputDirectory );
		outputDirectory.mkdirs();

		File target = new File( outputDirectory.getAbsolutePath() + File.separator + experimentFile.getName() );
		if( ! target.isFile() ){
			log.info( "Copying experiment-template to {}", target );

			FileInputStream in = new FileInputStream( experimentFile );
			FileOutputStream fos = new FileOutputStream( target );
			int read = 0;
			byte[] buf = new byte[1024];
			do {
				read = in.read( buf );
				if( read > 0 )
					fos.write( buf, 0, read );
			} while( read > 0 );
			in.close();
			fos.close();
		}

		//
		// initialize evaluation
		//
		//QuantileEstimatorTest eval = new QuantileEstimatorTest( new ExactQuantiles() );
		if( evaluation == null ){
			log.info( "No evaluation exists, creating empty evaluation" );
			AbstractTest<Data,Learner<Data,?>> evaluator = null;
			evaluation = new TestAndTrain<Data,Learner<Data,?>>( null, evaluator );
		}

		log.info( "Setting up plotter..., learner names: {}", evaluation.getLearnerNames() );

		StreamPlotter memoryPlot = new StreamPlotter( "Events", evaluation.getLearnerNames(), new File( this.getOutputDirectory().getAbsolutePath() + File.separator + "memory.png" ) );
		memoryPlot.setTitle( "Memory Usage" );
		if( updatePlots ){
			plots.add( memoryPlot );
			evaluation.addMemoryListener( memoryPlot );
		}
		File memoryStats = new File( getOutputDirectory().getAbsolutePath() + File.separator + "memory.dat" );
		log.info( "  writing memory statistics to {}", memoryStats );
		evaluation.addMemoryListener( new StatisticsStreamWriter( memoryStats ) );


		List<String> errors = new LinkedList<String>();
		for( String learner : evaluation.getLearnerNames() )
			errors.add( "Error(" + learner + ")" );

				StreamPlotter errorPlot = new StreamPlotter( "Events", errors, new File( this.getOutputDirectory().getAbsolutePath() + File.separator + "model-error.png" ) );
				errorPlot.setTitle( "Model Error" );

				if( updatePlots ){
					plots.add( errorPlot );
					evaluation.addPerformanceListener( errorPlot );
				}

				File modelError = new File( getOutputDirectory().getAbsolutePath() + File.separator + "model-error.dat" );
				log.info( "  writing model-error statistics to {}", modelError );

				int windowSize = 1000;
				try {
					windowSize = Integer.parseInt( this.getParameter( "windowSize" ) );
				} catch (Exception e) {
					if( log.isDebugEnabled() )
						e.printStackTrace();
					windowSize = 1000;
				}

				evaluation.addPerformanceListener( new WindowedStatisticsListener( new StatisticsStreamWriter( modelError ), windowSize, true ) ); //new File( this.getOutputDirectory().getAbsolutePath() + File.separator + "model-error.dat" ) ) );
				evaluation.addPerformanceListener( new MultiStatisticsWriter( getOutputDirectory() ) );


				for( String key : this.learner.keySet() ){
					log.info( "Initializing learner {}", key );
					Learner<?,?> learner = this.learner.get( key );
					learner.init();
				}


				//
				// Set up VM-Memory plotter
				//
				MemoryMonitor mem = new MemoryMonitor();

				Map<String,String> memConfig = new HashMap<String,String>();
				for( String str : this.params.keySet() ){
					if( str.startsWith( "memory." ) )
						memConfig.put( str.substring( "memory.".length() ), params.get( str ) );
				}
				ParameterInjection.inject( mem, memConfig );
				log.info( "Checking VM memory every {} milliseconds", mem.getTestInterval() );

				StreamPlotter vmMemoryPlot = new StreamPlotter( new File( this.getOutputDirectory().getAbsolutePath() + File.separator + "vm-memory.log" ) );
				vmMemoryPlot.setTitle( "JVM Memory Usage" );
				vmMemoryPlot.setRangeTitle( "Memory (bytes)" );
				vmMemoryPlot.setDomainTitle( "Time" );
				vmMemoryPlot.setUpdateInterval( 10 );
				mem.addMemoryListener( vmMemoryPlot );
				mem.start();
	}

	public void run() throws Exception {
		//
		// Initialize data-stream...
		//

		String streamName = evaluation.getInput();
		if( streamName != null && dataSources.containsKey( streamName ) ){
			log.info( "Using named stream '{}'", streamName );
			stream = dataSources.get( streamName ).createDataStream();
		}

		if( processor != null ){
			log.info( "Opening data stream {}", dataSource );
			stream = dataSource.createDataStream();
			processor.setSource( stream );
			log.info( "Replacing data stream by processor-chain: {}" , processor );
			stream = processor;
		}

		if( stream == null ){
			log.info( "Data stream not yet initialized, opening dataSource {}", dataSource );
			stream = this.dataSource.createDataStream();
		}

		long startTime = System.currentTimeMillis();
		//
		// The experiment loop:  read from the stream and test/train on each element
		//
		Data item = new DataImpl();
		Data datum = stream.readNext( item );
		long i = 0;
		while( i < start ){
			stream.readNext();
			i++;
		}
		log.info( "Skipped first {} data stream items, due to experiment range settings", i );


		log.info( "Starting GNUplot runner...") ;
		GnuplotRunner gnuplot = new GnuplotRunner( getOutputDirectory(), 1000L * plotInterval );
		if( this.getGnuplot() )
			gnuplot.start();

		StreamSummarizer summarizer = new StreamSummarizer(); // new File( getOutputDirectory().getAbsolutePath() + File.separator + "stream-statistics.html" ) );
		this.outputElements.put( "STREAM_SUMMARY", summarizer );
		preEvaluation.add( summarizer );

		outputElements.put( "CONFUSION_MATRIX", new PredictionResults<Data>( evaluation ) );

		int mark = 1000;
		if( log.isDebugEnabled() )
			mark = 100;

		updateHtml();
		i = 0;
		while( datum != null && ++i < limit ){
			try {

				for( DataStreamListener l : preEvaluation ){
					l.dataArrived( datum );
				}
				try {
					evaluation.test( datum );
				} catch (Exception e) {
					log.error( "Testing failed: {}", e.getMessage() );
					if( log.isDebugEnabled() )
						e.printStackTrace();
				}

				evaluation.train( datum );
				datum = stream.readNext( datum );

				if( i % mark == 0 ){
					long mem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					log.info( "Processed {} elements, currently using {}k of memory.", i, mem1 / 1024 );
					updateHtml();
				}

			} catch (Exception e) {
				log.error( "Error: {}", e );
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();

		if( this.getGnuplot() ){
			gnuplot.shutdown();
			gnuplot.join();
		}
		updateHtml();

		Long seconds = (endTime - startTime) / 1000;
		log.info( "Experiment finished after {} ms", ( endTime - startTime ) );
		log.info( "{} data items processed ({} items/sec).", i, i / seconds.doubleValue() );
	}


	public Statistics getVMMemoryUsage(){
		Statistics st = new Statistics();
		st.put( "TIME", new Double( System.currentTimeMillis() ) );
		Long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		st.put( "JVM-Memory", mem.doubleValue() );
		return st;
	}


	public void updateHtml() {
		try {
			String result = htmlOutput;

			for( String element : outputElements.keySet() ){
				if( result.indexOf( element ) >= 0 ){
					log.debug( "Found element {} at position: {}", element, result.indexOf( element ) );
				} else
					log.debug( "Did not find element {} in HTML result!", element);
				result = result.replaceAll( element, outputElements.get( element ).toHtml() );
			}

			PrintStream out = new PrintStream( new FileOutputStream( this.getOutputDirectory().getAbsolutePath() + File.separator + "index.html" ) );
			out.println( result );
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main( String[] args ) throws Exception {

		ExperimentRunner.main( args );
		/*

		if( args.length == 0 ){
			log.error( "Experiment-file required!" );
			return;
		}

		File file = new File( args[0] );

		if( !file.canRead() ){
			log.error( "Cannot read experiment-file {}!", file );
			return;
		}

		try {
			log.info( "Running experiment from file {}", file );

			if( ! file.isAbsolute() ){
				file = new File( (new File( "." ).getAbsolutePath() + File.separator + file.getPath() ) );
				log.info( "File is a relative file, using: {}", file );
			}
			Experiment e = ExperimentFactory.parseExperiment( file );
			e.init();
			e.run();
		} catch (Exception e) {
			log.error( "Execution of experiment failed: {}", e );
			e.printStackTrace();
		}
		 */
	}
}