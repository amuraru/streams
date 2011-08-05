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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author chris
 *
 */
public class ClassPanel extends JPanel {

	/** The unique class ID */
	private static final long serialVersionUID = 1102726291469499455L;


	JFreeChart chart = null;
	JPanel plot = new JPanel();
	JPanel settings = new JPanel();
	DefaultCategoryDataset classes = new DefaultCategoryDataset();
	Map<String,Double> classWeight = new LinkedHashMap<String,Double>();
	
	public ClassPanel( List<String> labels ){
		this.setLayout( new BorderLayout() );
		this.add( plot, BorderLayout.CENTER );
		this.add( settings, BorderLayout.SOUTH );
		settings.setLayout( new BoxLayout( settings, BoxLayout.Y_AXIS ) ); // new GridLayout(2,1) );
		
		chart = ChartFactory.createBarChart( "", "", "", classes, PlotOrientation.VERTICAL, false, false, false);
		ChartPanel chartPanel = new ChartPanel( chart );
		plot.add( chartPanel, BorderLayout.CENTER );
		
		for( String l : labels )
			addClass( l );
		plot.setBackground( Color.WHITE );
		
	}

	
	
	public void addClass( final String name ){
		
		this.classes.setValue( 100.0d, name, "Label" );
		this.classWeight.put( name, 100.0d );
		
		JPanel p = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		final JLabel label = new JLabel( "Class:" );
		p.add( label );
		JTextField classField = new JTextField( 10 );
		classField.setText( name );
		classField.setEditable( false );
		p.add( classField );
		final JSlider mean = new JSlider();
		
		final JTextField value = new JTextField( 6 );
		value.setText( classWeight.get( name ) + "" );
		value.addFocusListener( new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					Double v = new Double( value.getText() );
					classWeight.put( name, v );
					classes.setValue( v, name, "Label" );
					mean.setValue( v.intValue() );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		p.add( value );
		
		JButton button = new JButton( "Remove" );
		button.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		p.add( button );
		
		mean.setMinimum( 1 );
		mean.setMaximum( 10000 );
		mean.setValue( 1000 );
		
		mean.addChangeListener( new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				Double w = new Double( mean.getValue() ) / 10.0d;
				classWeight.put( name, w );
				classes.setValue( w, name, "Label" );
				value.setText( classWeight.get( name) + "" );
			}
		});
		p.add( mean );
		
		settings.add( p );
		chart.fireChartChanged();
	}

	
	public Map<String,Double> getClassWeights(){
		return this.classWeight;
	}
	
	
	public Set<String> getLabels(){
		return classWeight.keySet();
	}
	
	public void clear(){
		classes.clear();
		classWeight.clear();
		settings.removeAll();
	}
}