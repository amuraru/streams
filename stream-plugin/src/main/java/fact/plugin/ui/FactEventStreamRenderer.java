/**
 * 
 */
package fact.plugin.ui;

import java.awt.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;

/**
 * @author chris
 *
 */
public class FactEventStreamRenderer 
	extends AbstractRenderer 
{
	static Logger log = LoggerFactory.getLogger( FactEventStreamRenderer.class );

	/* (non-Javadoc)
	 * @see com.rapidminer.gui.renderer.Renderer#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.rapidminer.gui.renderer.Renderer#getVisualizationComponent(java.lang.Object, com.rapidminer.operator.IOContainer)
	 */
	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {

		
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.rapidminer.gui.renderer.Renderer#createReportable(java.lang.Object, com.rapidminer.operator.IOContainer, int, int)
	 */
	@Override
	public Reportable createReportable(Object renderable,
			IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		// TODO Auto-generated method stub
		return null;
	}
	

	/**
	 * @see com.rapidminer.gui.renderer.DefaultTextRenderer#getVisualizationComponent(java.lang.Object, com.rapidminer.operator.IOContainer)
	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		
		if( renderable instanceof FactEventObject ){
			
			log.info( "Need to render FactEventObject!" );
			
			FactEventObject factEvent = (FactEventObject) renderable;
			Data data = factEvent.getWrappedDataItem();
		} else {
			log.info( "Don't know how to render object {}", renderable );
		}
		
		return super.getVisualizationComponent(renderable, ioContainer);
	}
	 */
}