/**
 * 
 */
package fact.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.Serializable;
import java.lang.reflect.Array;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.gui.renderer.DefaultComponentRenderable;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;

import fact.FactViewerPanel;
import fact.plugin.FactEventObject;

/**
 * @author chris
 *
 */
public class FactEventObjectRenderer 
	extends AbstractRenderer
{
	static Logger log = LoggerFactory.getLogger( FactEventObjectRenderer.class );

	Component component;
	FactEventObject event;
	

	/**
	 * @see com.rapidminer.gui.renderer.DefaultTextRenderer#getVisualizationComponent(java.lang.Object, com.rapidminer.operator.IOContainer)
	 */
	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		
		if( renderable instanceof FactEventObject ){
			
			log.debug( "Need to render FactEventObject!" );
			event = (FactEventObject) renderable;
			
			FactViewerPanel eventPanel = new FactViewerPanel();
			eventPanel.setEvent( event.getWrappedDataItem() );
			component = eventPanel;
			
			JEditorPane resultText = new JEditorPane();
			resultText.setContentType("text/html");
			resultText.setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 11, 11, 11));
			resultText.setEditable(false);
			resultText.setBackground((new JLabel()).getBackground());

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
			resultText.setText( s.toString() );
			JPanel panel = new JPanel( new BorderLayout() );
			panel.add( resultText, BorderLayout.NORTH );
			panel.add( eventPanel, BorderLayout.CENTER );
			
			component = new JScrollPane( panel );
			
		} else {
			log.info( "Don't know how to render object {}", renderable );
			JTextArea area = new JTextArea();
			area.setEditable( false );
			area.setText( "FactEvent\\" );
			component = area;
		}
		
		return component;
	}


	/**
	 * @see com.rapidminer.gui.renderer.Renderer#getName()
	 */
	@Override
	public String getName() {
		return "FactEventRenderer";
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