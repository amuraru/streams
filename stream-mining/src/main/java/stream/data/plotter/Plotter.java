/**
 * 
 */
package stream.data.plotter;

import java.awt.Dimension;

/**
 * @author chris
 *
 */
public interface Plotter {

	
	public Dimension getSize();
	public void setSize( Dimension d );
	
	
	public String getTitle();
	public void setTitle( String title );

	
	public String getDescription();
	public void setDescription( String description );
	
	
	public String createHtmlEmbedding();
	
	
	public void updateChart();
}
