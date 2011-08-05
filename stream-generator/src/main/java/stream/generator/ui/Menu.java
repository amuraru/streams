/**
 * 
 */
package stream.generator.ui;

import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author chris
 *
 */
public class Menu
	extends JMenuBar
{
	/** The unique class ID */
	private static final long serialVersionUID = -5551092855482018772L;
	Map<String,JMenu> menues = new LinkedHashMap<String,JMenu>();
	
	
	public Menu(){
		add( "File" );
	}
	
	
	public void add( String menuName ){
		getMenu( menuName );
	}
	
	
	public void add( String name, String itemName, ActionListener l ){
		JMenu menu = getMenu(name);
		
		JMenuItem item = new JMenuItem( itemName );
		item.addActionListener( l );
		menu.add( item );
	}
	
	public JMenu getMenu( String name ){
		JMenu m = menues.get( name );
		if( m == null ){
			m = new JMenu( name );
			menues.put( name, m );
			this.add( m );
		}
		return m;
	}
}