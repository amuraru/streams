/**
 * 
 */
package stream.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.Serializable;
import java.lang.reflect.Array;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.plugin.DataObject;

import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.gui.renderer.DefaultComponentRenderable;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;

/**
 * @author chris
 *
 */
public class DataObjectRenderer extends AbstractRenderer
{
	static Logger log = LoggerFactory.getLogger( DataObjectRenderer.class );

	Component component;
	DataObject event;
	

	/**
	 * @see com.rapidminer.gui.renderer.DefaultTextRenderer#getVisualizationComponent(java.lang.Object, com.rapidminer.operator.IOContainer)
	 */
	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		
		JEditorPane resultText = new JEditorPane();
		resultText.setContentType("text/html");
		resultText.setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 11, 11, 11));
		resultText.setEditable(false);
		resultText.setBackground((new JLabel()).getBackground());
		
		
		if( renderable instanceof DataObject ){
			log.debug( "Need to render FactEventObject!" );
			event = (DataObject) renderable;
			resultText.setText( toHtml( event ) );
		} else {
			log.info( "Don't know how to render object {} of class {}", renderable, renderable.getClass() );
			resultText.setText( toHtml( renderable ) );
		}

		JPanel panel = new JPanel( new BorderLayout() );
		panel.add( resultText, BorderLayout.NORTH );
		//panel.add( eventPanel, BorderLayout.CENTER );
		
		component = new JScrollPane( panel );
		return component;
	}

	
	
	public String toHtml( Object object ){
		StringBuffer s = new StringBuffer( "<html><body>" );
		s.append( "<h1>" + object.getClass() + "</h1>\n" );
		s.append( "<pre>" );
		s.append( object.toString() );
		s.append( "</pre>" );
		s.append( "</body></html>" );
		return s.toString();
	}
	
	
	public String toHtml( DataObject event ){

		StringBuffer s = new StringBuffer( "<html>" );
		s.append( "<table>" );
		for( String key : event.keySet() ){
			s.append( "<tr>" );
			s.append( "<td><b>" + key + "</b></td>" );
			s.append( "<td><code>" );
			Serializable val = event.get( key );
			if( val.getClass().isArray() ){
				Class<?> type = val.getClass().getComponentType();
				int len = Array.getLength( val );
				s.append( type.getName() + "[" + len + "]" );
				
				try {
					s.append( "  (values: " );
					for( int i = 0; i < len && i < 4; i++ ){
						Object o = Array.get( val, i );
						if( o == null )
							s.append( "null" );
						else
							s.append( o.toString() );
						
						if( i + 1 < len )
							s.append( ", " );
					}
					s.append( "...)" );
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				s.append( val.toString() );
			}
			s.append( "</code></td></tr>\n" );
		}
		s.append( "</table>" );
		s.append( "</html>" );
		return s.toString();
	}
	

	/**
	 * @see com.rapidminer.gui.renderer.Renderer#getName()
	 */
	@Override
	public String getName() {
		return "DataObjectRenderer";
	}


	/* (non-Javadoc)
	 * @see com.rapidminer.gui.renderer.Renderer#createReportable(java.lang.Object, com.rapidminer.operator.IOContainer, int, int)
	 */
	@Override
	public Reportable createReportable(Object renderable, IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		log.info( "Calling createReportable()..." );
		return new DefaultComponentRenderable( component );
	}
}