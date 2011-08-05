/**
 * 
 */
package stream.generator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import stream.generator.Gaussian;
import stream.generator.LabeledGaussianStream;
import stream.generator.StreamGenerator;
import stream.io.DataStream;
import stream.io.DataStreamWriter;

/**
 * @author chris
 *
 */
public class StreamEditor extends JFrame {

	/** The unique class ID */
	private static final long serialVersionUID = 5672992672202735327L;
	JComboBox attributeList = new JComboBox();

	JPanel buttons = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
	JTabbedPane tabs = new JTabbedPane();
	ClassPanel classPanel;
	CovariancePanel covariancePanel = new CovariancePanel();
	
	List<String> labels = new ArrayList<String>();
	List<GaussPanel> attributePanels = new ArrayList<GaussPanel>();
	File file = null;

	public StreamEditor(){
		this.setTitle( "Stream Editor" );
		this.setSize( 1024, 800 );
		this.getContentPane().setLayout( new BorderLayout() );

		Menu menu = new Menu();

		menu.add( "File", "Open File", new ActionListener(){
			public void actionPerformed( ActionEvent e){
				openFile();
			}
		});
		
		menu.add( "File", "Save", new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				saveConfig( file );
			}
		});
		
		menu.add( "File", "Save As...", new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				saveConfig( null );
			}
		});
		

		menu.add( "File", "Quit", new ActionListener(){
			public void actionPerformed( ActionEvent e){
				quit();
			}
		});

		getContentPane().add( menu, BorderLayout.NORTH );


		menu.add( "Data Set", "Add Class", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addClass( "Class_" + System.currentTimeMillis() );
			}
		});

		menu.add( "Data Set", "Add Attribute", new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addAttribute( "attr" + attributePanels.size() );
			}
		});
		
		menu.add( "Data Set", "Generate Data", new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				generateData();
			}
		});

		
		classPanel = new ClassPanel( labels );

		addClass( "positive" );
		addClass( "negative" );
		tabs.add( "Class Proportions", classPanel );
		tabs.add( "Covariance Matrix", covariancePanel );

		for( int i = 0; i < 3; i++ ){
			String attr = "attr" + i;
			addAttribute( attr );
		}

		this.getContentPane().add( tabs, BorderLayout.CENTER );
	}


	public void addAttribute( String name ){
		GaussPanel p =  new GaussPanel( name );
		for( String l : classPanel.getLabels() )
			p.addGaussian( l, new Gaussian() );
		//tabs.add( name, p );
		int idx = Math.max( 1, tabs.getTabCount() - 1 );
		tabs.add( p, idx );
		tabs.setTitleAt( idx, name );
		attributePanels.add( p );
		
		covariancePanel.addAttribute( name );
	}

	public void addClass( String name ){
		
		if( !labels.contains( name ) )
			labels.add( name );

		classPanel.addClass( name );
		covariancePanel.addClass( name );
		for( GaussPanel p : attributePanels )
			p.addGaussian( name, new Gaussian() );
	}


	public void setStreamConfig( LabeledGaussianStream c ){

		classPanel.clear();
		attributePanels.clear();
		tabs.removeAll();
		tabs.add( "Class Proportions", classPanel );

		for( String label : c.getClassOracle().getLabels() )
			addClass( label );

		for( String attribute : c.getAttributes().keySet() ){
			addAttribute( attribute );
		}

		for( GaussPanel p : attributePanels )
			p.clear();


		for( String label : c.getClassOracle().getLabels() ){
			for( String attribute : c.getAttributes().keySet() ){
				GaussPanel p = getAttributePanel( attribute );
				p.addGaussian( label, c.getGaussian( label, attribute ) );
			}
		}
	}

	public GaussPanel getAttributePanel( String attribute ){
		for( GaussPanel p : attributePanels ){
			if( attribute.equals( p.attributeName ) ){
				return p;
			}
		}
		return null;
	}

	public void openConfig( File file ) throws Exception {
		setStreamConfig( LabeledGaussianStream.createFromFile( file ) );
		this.file = file;
		setTitle( "StreamEditor - " + file.getAbsolutePath() );
	}

	public void saveConfig( File file ) {
		if( file != null ){
			try {
				//System.out.println( "Writing config to " + file.getAbsolutePath() );
				PrintStream out = new PrintStream( new FileOutputStream( file ) );
				out.println( getXML() );
				out.close();
				this.file = file;
				this.setTitle( "StreamEditor - " + file.getAbsolutePath() );
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			
			JFileChooser jfc = new JFileChooser();
			jfc.showSaveDialog( this );
			File f = jfc.getSelectedFile();
			if( f != null ){
				saveConfig( f );
			}
		}
	}

	
	public void generateData(){
	
		JFileChooser jfc = new JFileChooser();
		jfc.showSaveDialog( this );
		
		File out = jfc.getSelectedFile();
		if( out != null ){
			
			try {
				LabeledGaussianStream stream = LabeledGaussianStream.createFromXML( getXML() );
				StreamGenerator gen = new StreamGenerator();
				gen.setSource( stream );
				
				DataStreamWriter w = new DataStreamWriter( out, ";" );
				gen.setDestination( w );
				
				
				Long limit = 10000L;
				try {
					limit = new Long( System.getProperty( "limit" ) );
				} catch (Exception e) {
					limit = 10000L;
				}
				
				gen.setLimit( limit );
				ProgressDialog d = new ProgressDialog( "Generating Data..." );
				gen.addTaskListener( d );
				gen.start();
				d.setVisible( true );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public DataStream createStream(){
		try {
			LabeledGaussianStream stream = LabeledGaussianStream.createFromXML( getXML() );
			return stream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public String getXML(){

		StringBuffer s = new StringBuffer();
		s.append( "<Stream class=\"stream.generator.LabeledGaussianStream\">\n" );


		Double totalWeight = 0.0d;
		for( String clazz : classPanel.getLabels() )
			totalWeight += classPanel.getClassWeights().get( clazz );


		for( String clazz : classPanel.getLabels() ){
			Double weight = classPanel.getClassWeights().get( clazz ) / totalWeight;
			s.append( "  <Class name=\"" + clazz + "\" weight=\"" + weight + "\">\n" );
			for( GaussPanel p : this.attributePanels ){
				Gaussian g = p.getGenerators().get( clazz );
				s.append( "    <Attribute name=\"" + p.attributeName + "\" class=\"stream.generator.Gaussian\" mean=\"" + g.getMean() + "\" variance=\"" +  g.getVariance() + "\" />\n" );
			}
			s.append( "  </Class>\n\n" );
		}

		s.append( "</Stream>");
		return s.toString();
	}


	public void openFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog( null );
		File file = jfc.getSelectedFile();
		if( file != null && file.getName().endsWith( ".xml" ) ){
			try {
				openConfig( file );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void quit(){
		System.exit( 0 );
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		StreamEditor editor = new StreamEditor();

		if( args.length > 0 ){
			File file = new File( args[0] );
			editor.openConfig( file );
		}

		editor.setVisible( true );
	}
}