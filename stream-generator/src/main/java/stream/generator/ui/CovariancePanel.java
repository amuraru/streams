/**
 * 
 */
package stream.generator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * @author chris
 *
 */
public class CovariancePanel extends JPanel implements ItemListener {

	/** The unique class ID */
	private static final long serialVersionUID = -2373755701992059703L;

	List<String> attributes = new ArrayList<String>();
	Map<String,CovarianceTable> tables = new LinkedHashMap<String,CovarianceTable>();
	JComboBox classSelector = new JComboBox();
	JPanel tp = new JPanel();
	
	public CovariancePanel(){
		setLayout( new BorderLayout() );
		
		classSelector.addItemListener( this );
		
		JPanel select = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		select.add( new JLabel( "Class:" ) );
		select.add( classSelector );
		
		add( select, BorderLayout.NORTH );
		
		tp.setLayout( new BorderLayout() );
		add( tp, BorderLayout.CENTER );
	}
	
	
	public void addAttribute( String name ){
		for( CovarianceTable matrix : tables.values() )
			matrix.addAttribute( name );
	}

	
	public void addClass( String clazz ){
		if( tables.containsKey( clazz ) )
			return;
		
		classSelector.addItem( clazz );
		CovarianceTable table = new CovarianceTable( attributes );
		tables.put( clazz, table );
		showTable( clazz );
	}
	

	/**
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if( arg0.getItem() != null ){
			
			String clazz = arg0.getItem().toString();
			if( tables.containsKey( clazz ) )
				showTable( clazz );
		}
	}
	
	
	public void showTable( String clazz ){
		if( !tables.containsKey( clazz ) )
			return;
		
		GridBagLayout g = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JTable table = new JTable( tables.get( clazz ) );
		
		table.setBorder( BorderFactory.createLineBorder( Color.DARK_GRAY ) );
		table.setRowHeight( 48 );
		for( int i = 0; i < table.getColumnCount(); i++ ){
			TableColumn col = table.getColumnModel().getColumn( i );
			col.setPreferredWidth( 64 );
		}
		table.setGridColor( Color.LIGHT_GRAY );
		table.setDefaultRenderer( Double.class, new DoubleCellRenderer() );
		tp.removeAll();
		tp.setLayout( g );
		g.setConstraints( table, c );
		tp.add( table );
		tp.repaint();
		this.repaint();
	}
	
	
	public class DoubleCellRenderer extends DefaultTableCellRenderer {
		/**  		 */
		private static final long serialVersionUID = -4558254644308133312L;

		/**
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			super.getTableCellRendererComponent( arg0, arg1, arg2, arg3, arg4, arg5 );
			this.setAlignmentX( Component.CENTER_ALIGNMENT );
			return this;
		}
	}
}