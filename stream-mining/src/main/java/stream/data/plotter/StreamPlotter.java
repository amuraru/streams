/**
 * 
 */
package stream.data.plotter;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.stats.Statistics;
import stream.data.stats.StatisticsListener;

/**
 * <p>
 * This class implements a plotter for statistics. It basically wraps the JFreeChart plotter
 * and updates the plot on a predefined update-interval.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class StreamPlotter
	extends AbstractPlotter
	implements StatisticsListener, Plotter 	
{
	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( StreamPlotter.class );
	
	String timeKey;
	String refKey = null;
	File outputFile;
	List<String> seriesNames;
	Double defaultValue = Double.NaN;
	PrintStream p;
	Map<String,XYSeries> seriesData = new HashMap<String,XYSeries>();
	Map<String,String> properties = new HashMap<String,String>();
	int plotterCount = 0;
	
	public StreamPlotter( String timeKey, Collection<String> keys, File out ) {
		super( "" );
		this.timeKey = timeKey;
		this.seriesNames = new ArrayList<String>( keys );
		this.outputFile = out;
	}
	
	public StreamPlotter( File out ){
		super( "" );
		this.timeKey = null;
		this.seriesNames = null;
		this.outputFile = out;
	}
	
	
	public void setParameter( String key, String value ){
		this.properties.put( key, value );
	}

	public String getParameter( String key ){
		return properties.get( key );
	}
	
	public void setReferenceKey( String key ){
		this.refKey = key;
	}
	
	public void setSeriesKeys( List<String> series ){
		this.seriesNames = new ArrayList<String>( series );
	}

	public void init( Map<String,?> datum ){
		log.info( "Series keys: {}", seriesNames );
		log.info( "Initializing x-y series, datum keys are: {}", datum );
		if( timeKey == null ){
			
			if( datum.keySet().isEmpty() ){
				log.error( "No series-keys available in data!" );
				return;
			} else {
				timeKey = datum.keySet().iterator().next();
				if( rangeTitle == null || rangeTitle.isEmpty() )
					this.rangeTitle = timeKey;
				log.info( "Auto-detected timeKey (domain-axis) to be '{}'", timeKey );
			}
		}
		
		Double events = (Double) datum.get( timeKey );
		
		if( seriesNames == null || seriesNames.isEmpty() ){
			if( seriesNames == null )
				seriesNames = new ArrayList<String>( datum.keySet().size() - 1 );
				
			for( String k : datum.keySet() ){
				if( ! k.equals( timeKey ) )
					seriesNames.add( k );
			}
			
			log.info( "Plotting series keys: {}", seriesNames );
		}
		
		//Collections.sort( seriesNames );
		log.info( "    seriesNames are: {}", seriesNames );
		
		if( refKey != null && ! seriesNames.contains( refKey ) ){
			log.error( "Reference-key '{}' is not provided by data-source! Disabling relative chart...", refKey );
			refKey = null;
		}
		
		for( String key : seriesNames ){
			log.debug( "need to create series for key '{}'", key );
			if( ! timeKey.equals(key) && ! seriesData.containsKey( key ) ){
				XYSeries ts = new XYSeries( key );
				ts.setMaximumItemCount( this.getHistory() );
				this.seriesData.put( key, ts );
				if( events == null )
					ts.add( 0, new Double( 0.0d ) );
				else
					ts.add( events, new Double( 0.0d ) );
			}
		}
	}

	
	public void dataArrived( Data datum ){
	}

	/**
	 * @see stream.io.DataStreamListener#dataArrived(java.util.Map)
	 */
	@Override
	public void dataArrived(Statistics datum) {
		if( timeKey == null || seriesData.isEmpty() ){
			init( datum );
			return;
		}
		
		Double x = (Double) datum.get( timeKey );
		
		for( String key : this.seriesNames ){
			XYSeries ts = seriesData.get( key );
			if( ts != null ){
				if( datum.getName() != null && !datum.getName().isEmpty() )
					ts.setDescription( datum.getName() );
				
				try {
					Double val = (Double) datum.get( key );
					
					if( refKey != null )
						val = val / ((Double) datum.get( refKey ));
					
					ts.add( x, val);
					//ts.update( x, val );
				} catch (Exception e){
					log.error( "Failed to add item ({},{}) to xy-series '" + key + "'", x, datum.get( key ) );
				}
			}
		}
		
		if( plotterCount % getUpdateInterval() == 0 )
			updateChart();
		
		plotterCount++;
	}
	
	
	public void updateChart(){
		log.debug( "Updating chart" );
		try {
			
			Color[] colors = new Color[]{
					Color.RED,
					Color.BLUE,
					Color.GREEN,
					Color.MAGENTA,
					Color.ORANGE,
					Color.CYAN,
					Color.PINK
			};
			
			XYSeriesCollection dataset = new XYSeriesCollection();
			for( String key : seriesNames ) // seriesData.keySet() )
				dataset.addSeries( seriesData.get( key ) );

			
			JFreeChart chart = ChartFactory.createXYLineChart(
					this.getTitle(),
					getDomainTitle(), 
					getRangeTitle(), 
					dataset, 
					PlotOrientation.VERTICAL,
					true, 
					false, 
					false);
			
			chart.getTitle().setFont( new Font( Font.SERIF, Font.BOLD, 20 ) );
			chart.getTitle().setPaint( Color.DARK_GRAY );

			
			XYPlot plot = (XYPlot) chart.getXYPlot();
			plot.setBackgroundPaint( Color.WHITE );
			plot.setDomainGridlinePaint( Color.GRAY );
			plot.setDomainCrosshairVisible( true );
			plot.setRangeCrosshairVisible( true );
			
			for( int i = 0; i < dataset.getSeriesCount(); i++ ){
				String desc = dataset.getSeries(i).getDescription();
				if( desc != null )
					plot.getLegendItems().add( new LegendItem( desc ) );
				plot.getRendererForDataset( dataset ).setSeriesPaint( i, colors[i % colors.length]);
			}

			File tmp = new File( this.outputFile.getAbsolutePath() + ".tmp" + System.currentTimeMillis() );
			log.debug( "Writing chart to file {}", outputFile );
			File target = outputFile;
			if( ! target.getName().endsWith( ".png" ) )
				target = new File( target.getAbsolutePath() + ".png" );
			ChartUtilities.saveChartAsPNG( tmp, chart, getSize().width, getSize().height );
			tmp.renameTo( target );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String createHtmlEmbedding(){
		StringBuffer b = new StringBuffer();
		b.append( "<div class=\"section\">\n" );
		//b.append( "  <h3>" + getTitle() + "</h3>\n" );
		if( getDescription() != null && !getDescription().trim().isEmpty() )
			b.append( "  <p>\n    " + getDescription() + "\n  </p>\n" );
 		
		b.append( "  <div class=\"figure\">\n" );
		b.append( "     <img src=\"" + outputFile.getName() + "\" border=\"0\" align=\"center\"/>" );
		b.append( "  </div>" );
		b.append( "</div>\n" );
		return b.toString();
	}
}