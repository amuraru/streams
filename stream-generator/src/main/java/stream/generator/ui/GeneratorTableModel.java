/**
 * 
 */
package stream.generator.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import stream.generator.Gaussian;

/**
 * @author chris
 *
 */
public class GeneratorTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8208367050397068391L;

	String[] columnNames = new String[]{ "Class", "Mean", "Variance" }; 
	Map<String,Gaussian> gaussians = new LinkedHashMap<String,Gaussian>();

	public GeneratorTableModel(){
	}
	
	public GeneratorTableModel( Map<String,Gaussian> gs ){
		gaussians.putAll( gs );
	}

	public void add( String name, Gaussian g ){
		gaussians.put( name, g );
		this.fireTableDataChanged();
	}
	
	
	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int arg0) {
		if( arg0 == 0 )
			return String.class;
		return Double.class;
	}
	
	

	/**
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int arg0) {
		return columnNames[ arg0 ];
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return gaussians.size();
	}

	public String getRowKey( int row ){
		int i = 0;
		for( String key : gaussians.keySet() ){
			if( i == row )
				return key;
			i++;
		}
		return null;
	}
	
	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		
		String key = getRowKey( row );
		Gaussian g = gaussians.get( key );
		
		if( col == 0 )
			return key;
		
		if( col == 1 )
			return g.getMean();
		
		if( col == 2 )
			return g.getVariance();
		
		return "?";
	}
}