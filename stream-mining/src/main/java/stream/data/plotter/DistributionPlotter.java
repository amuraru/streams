/**
 * 
 */
package stream.data.plotter;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.io.DataStreamListener;
import stream.learner.LearnerUtils;
import stream.model.Distribution;
import stream.model.NominalDistributionModel;
import stream.model.NumericalDistributionModel;

/**
 * <p>
 * This plotter creates a plot of the distribution of real-valued attributes.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 * 
 */
public class DistributionPlotter 
extends AbstractPlotter
implements DataStreamListener, Plotter 
{
	static Logger log = LoggerFactory.getLogger( DistributionPlotter.class );

	File outputFile;
	Map<String,Distribution<Double>> numerics = new LinkedHashMap<String,Distribution<Double>>();
	Map<String,Distribution<String>> nominals = new LinkedHashMap<String,Distribution<String>>();
	Integer count = 0;
	int bins = 1000;

	public DistributionPlotter( int bins, File output ){
		super( "Distribution" );
		this.bins = bins;
		outputFile = output;
	}


	/**
	 * @see stream.io.DataStreamListener#dataArrived(java.util.Map)
	 */
	@Override
	public void dataArrived(Data data) {

		String label = LearnerUtils.detectLabelAttribute( data );

		for( String key : data.keySet() ){

			if( key.equals( label ) ){

			} else {

				if( data.get( key ).getClass().equals( Double.class ) ){
					//
					// put off handling of numericals for later...
					//
					Distribution<Double> dist = numerics.get( key );
					if( dist == null ){
						dist = new NumericalDistributionModel( bins, 0.1 );
						numerics.put( key, dist );
					}
					dist.update( (Double) data.get( key ) );

				} else {
					//
					// update nominal attributes
					//
					String item = data.get( key ).toString();
					Distribution<String> dist = nominals.get( key );
					if( dist == null ){
						dist = new NominalDistributionModel<String>();
						nominals.put( key, dist );
					}
					dist.update( item );
				}
			}
		}

		if( count % getUpdateInterval() == 0 )
			updateChart();
		count++;
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
			for( String key : numerics.keySet() ){
				Distribution<Double> m = numerics.get( key );
				XYSeries serie = new XYSeries( key );
				Map<?,Double> histogram = m.getHistogram();
				//int i = 0;
				for( Object k : histogram.keySet() ){
					//for( int i = 0; i < histogram.length; i++ ){
					//log.info( "histogram[{}] = {}", i, histogram[i] );
					Double x = 0.0d;

					if( k instanceof Double ){
						x = (Double) k; //m.get.getInterval() * i;
					}

					//log.info( "  P( x = {} ) = {}", x, histogram[i] );
					serie.add( x, histogram.get( k ) );
				}
				dataset.addSeries( serie );
			}

			DefaultCategoryDataset category = new DefaultCategoryDataset();
			for( String key : nominals.keySet() ){
				Distribution<String> m = nominals.get( key );
				Map<String,Double> h = m.getHistogram();
				for( String clazz : h.keySet() )
					category.addValue( h.get( clazz ), key, clazz );
			}


			JFreeChart chart = ChartFactory.createXYLineChart(
					this.getTitle(),
					"Values", 
					"Frequency", 
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
			//plot.setRenderer( new DeviationRenderer( true, false ) );
			//plot.setRenderer( new XYSplineRenderer( 1 ) );
			//plot.getDomainAxis().setLabelFont( new Font( Font.SERIF, Font.BOLD, 14 ) );
			//plot.getRangeAxis().setLabelFont( new Font( Font.SERIF, Font.BOLD, 14 ) );

			for( int i = 0; i < dataset.getSeriesCount(); i++ ){
				plot.getRendererForDataset( dataset ).setSeriesPaint( i, colors[i % colors.length].brighter() );
			}

			File tmp = new File( this.outputFile.getAbsolutePath() + ".tmp" + System.currentTimeMillis() );
			log.debug( "Writing chart to file {}", outputFile );

			ChartUtilities.saveChartAsPNG( tmp, chart, getSize().width, getSize().height );
			tmp.renameTo( outputFile );
			
			
			JFreeChart cat = ChartFactory.createBarChart( this.getTitle(), "Values", "Frequency", category, PlotOrientation.VERTICAL, true, false, false );
			File target = new File( outputFile.getParentFile().getAbsolutePath() + File.separator + "nominal-" + outputFile.getName() );
			ChartUtilities.saveChartAsPNG( tmp, cat, getSize().width, getSize().height );
			tmp.renameTo( target );


		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


	public String createHtmlEmbedding(){
		StringBuffer b = new StringBuffer();
		b.append( "<div class=\"section\">\n" );
		//b.append( "  <h3>" + "" + "</h3>\n" );
		b.append( "  <div class=\"figure\">\n" );
		b.append( "     <img src=\"" + outputFile.getName() + "\" border=\"0\" align=\"center\"/>" );
		b.append( "  </div>" );
		b.append( "</div>\n" );
		return b.toString();
	}
}