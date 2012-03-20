/**
 * 
 */
package fact.plugin.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import stream.data.Data;
import fact.viewer.ui.MapView;

/**
 * This panel provides UI elements for displaying FACT events.
 * 
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class FactEventPanel extends JPanel {

	/** The unique class ID */
	private static final long serialVersionUID = 7198347390707679305L;

	Data event;
	JTabbedPane tabs = new JTabbedPane();
	MapView cameraMap;
	
	public FactEventPanel( Data event ){
		this.event = event;
		
		this.setLayout( new BorderLayout() );
		
		cameraMap = new MapView();
		cameraMap.getMap().setEvent( event );
		//float[] pixelData = (float[]) event.get( "Data" );
		//cameraMap.getMap().setData( pixelData );
		
		tabs.addTab( "Camera", cameraMap );
		add( tabs, BorderLayout.CENTER );
	}
}
