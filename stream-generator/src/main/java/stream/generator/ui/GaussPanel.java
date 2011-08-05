/**
 * 
 */
package stream.generator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import stream.generator.DistributionFunction;
import stream.generator.Gaussian;

/**
 * @author chris
 *
 */
public class GaussPanel extends JPanel {


	/** The unique class ID */
	private static final long serialVersionUID = 8467868265150855811L;

	String attributeName;
	JFreeChart chart = null;
	JPanel plot = new JPanel();
	JPanel settings = new JPanel();

	//List<SeriesFunction> datasets = new ArrayList<SeriesFunction>();
	XYSeriesCollection functions = new XYSeriesCollection();
	Map<String,Gaussian> gaussians = new LinkedHashMap<String,Gaussian>();
	Map<String,SeriesFunction> datasets = new LinkedHashMap<String,SeriesFunction>();
	Map<String,JSlider> meanSlider = new LinkedHashMap<String,JSlider>();
	Map<String,JSlider> varSlider = new LinkedHashMap<String,JSlider>();
	GeneratorTableModel gs = new GeneratorTableModel();
	
	public GaussPanel( String attributeName ){
		this.attributeName = attributeName;
		this.setLayout( new BorderLayout() );
		settings.setLayout( new BoxLayout( settings, BoxLayout.Y_AXIS ) );
		
		chart = ChartFactory.createScatterPlot( "", "X", "Y", functions, PlotOrientation.VERTICAL, true, false, false );
		XYPlot xy = (XYPlot) chart.getPlot();
		xy.getDomainAxis().setAutoRange( true );
		StandardXYItemRenderer r = new StandardXYItemRenderer();
		r.setPlotLines( true );
		r.setPlotImages( false );
		xy.setRenderer( r );
		ChartPanel chartPanel = new ChartPanel( chart );
		chartPanel.setBorder( null );
		plot.setBackground( Color.WHITE );
		plot.add( chartPanel, BorderLayout.CENTER );
		
		JTable table = new JTable( gs );
		table.setBorder( null );
		table.setRowHeight( 32 );
		table.setShowGrid( true );
		table.setGridColor( Color.LIGHT_GRAY );
		
		JPanel tableContainer = new JPanel( new BorderLayout() );
		tableContainer.add( table.getTableHeader(), BorderLayout.NORTH );
		tableContainer.add( table, BorderLayout.CENTER );
		//settings.add( tableContainer );
		settings.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		
		JSplitPane split = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		split.setLeftComponent( plot );
		
		JScrollPane sc = new JScrollPane( settings );
		sc.setBorder( null );
		split.setRightComponent( sc );
		
		add( split, BorderLayout.CENTER );
		this.setBorder( null );
	}

	
	public void clear(){
		datasets.clear();
		varSlider.clear();
		meanSlider.clear();
		gaussians.clear();
		settings.removeAll();
		functions.removeAllSeries();
	}
	
	
	public void reset( String name ){
		if( gaussians.containsKey( name ) ){
			Gaussian g = this.gaussians.get( name );
			g.setMean( 0.0d );
			g.setVariance( 1.0d );
			varSlider.get( name ).setValue( 1000 );
			meanSlider.get( name ).setValue( 0 );
		}
		if( datasets.containsKey( name ) )
			datasets.get(name).update();
	}

	
	public void addGaussian( final String name, final Gaussian g ){
		final SeriesFunction f = new SeriesFunction( name, -20.0d, 20.0d, 1000, g );
		this.datasets.put( name, f );
		this.gaussians.put( name, g );
		gs.add( name, g );
		
		final JSlider mean = new JSlider();
		final JSlider variance = new JSlider();

		JPanel p = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		final JLabel l = new JLabel( "Class: " );
		p.add( l );
		JTextField classField = new JTextField( 10 );
		classField.setEditable( false );
		classField.setText( name );
		p.add( classField );

		final JTextField meanField = new JTextField( 6 );
		meanField.addFocusListener( new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					Double m = new Double(meanField.getText());
					Double d = m * 1000;
					mean.setValue( d.intValue() );
					g.setMean( m );
					f.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		p.add( new JLabel( "Mean:" ) );
		p.add( meanField );
		meanField.setText( g.getMean() + "" );
		
		final JTextField varField = new JTextField( 6 );
		varField.addFocusListener( new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					Double v = new Double( varField.getText() );
					Double sv = v * 1000.0d;
					variance.setValue( sv.intValue() );
					g.setVariance( v );
					f.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		p.add( new JLabel( "Variance: " ) );
		p.add( varField );
		varField.setText( g.getVariance() + "" );
		
		JButton button = new JButton( "Reset" );
		button.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset(name);
			}
		});
		p.add( button );

		mean.setMinimum( -20000 );
		mean.setMaximum( 20000 );
		mean.setValue( 0 );
		meanSlider.put( name, mean );
		mean.addChangeListener( new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				g.setMean( new Double( mean.getValue() ) / 1000.0d );
				//System.out.println( "Updating mean of series " + f.name + " to: " + g.getMean() );
				f.update();
				meanField.setText( g.getMean() + "" );
			}
		});
		p.add( mean );



		variance.setMinimum( 1 );
		variance.setMaximum( 10000 );
		variance.setValue( 1000 );
		varSlider.put( name, variance );
		variance.addChangeListener( new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				Double d = new Double( variance.getValue() ) / 1000.0d;
				g.setVariance( d );
				f.update();
				varField.setText( g.getVariance() + "" );
			}
		});
		p.add( variance );
		settings.add( p );
		this.functions.addSeries( f );
		XYPlot xy = (XYPlot) chart.getPlot();
		StandardXYItemRenderer r = new StandardXYItemRenderer();
		r.setPlotLines( true );
		r.setPlotImages( false );

		int idx = functions.getSeries().indexOf( f );
		if( idx >= 0 )
			xy.setRenderer( idx, r );

		this.validate();
		chart.fireChartChanged();
	}


	public void removeGaussian( String name ){
		if( ! gaussians.containsKey( name ) )
			return;
	}

	public void updateChart(){
		for( SeriesFunction f: this.datasets.values() ){
			f.update();
		}
	}

	
	public Map<String,Gaussian> getGenerators(){
		return this.gaussians;
	}
	

	public class SeriesFunction extends XYSeries {
		/** The unique class ID */
		private static final long serialVersionUID = -3512502500151796175L;
		String name;
		DistributionFunction df;
		Double delta = 0.1;
		Double min = 0.0;
		Double max = 1.0;
		Integer steps;

		public SeriesFunction( String name, Double xmin, Double xmax, Integer steps, DistributionFunction df ){
			super( name );
			this.df = df;
			this.min = xmin;
			this.max = xmax;
			this.delta = Math.abs(max - min) / steps.doubleValue();
			this.steps = steps;
			update();
		}

		public void update(){
			this.clear();
			//System.out.println( "DF: " + df );
			for( Integer i = 0; i < steps; i++ ){
				Double x = min + i * delta;
				this.add( x, df.p(x) );
				//System.out.println( x + " " + df.p(x ) );
			}
			this.fireSeriesChanged();
		}
	}
}