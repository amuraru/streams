/**
 * 
 */
package stream.generator.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.CholeskyDecomposition;

/**
 * @author chris
 *
 */
public class CovarianceTable extends AbstractTableModel {

	/** The unique class ID */
	private static final long serialVersionUID = -2373755701992059703L;

	List<String> attributes = new ArrayList<String>();
	DenseDoubleMatrix2D matrix = new DenseDoubleMatrix2D( 3, 3 );
	
	public CovarianceTable( List<String> attr ){
		attributes = new ArrayList<String>( attr );
		matrix = new DenseDoubleMatrix2D( attributes.size(), attributes.size() );
		for( int i = 0; i < matrix.rows(); i++ )
			for( int j = 0; j < matrix.columns(); j++ ){
				if( i == j )
					matrix.set( i ,j, 1.0d );
				else
					matrix.set( i, j, matrix.get(i, j) );
			}
		
		fireTableStructureChanged();
	}
	
	public void addAttribute( String name ){
		attributes.add( name );
		
		DenseDoubleMatrix2D m2 = new DenseDoubleMatrix2D( attributes.size(), attributes.size() );
		for( int i = 0; i < matrix.rows(); i++ )
			for( int j = 0; j < matrix.columns(); j++ )
				m2.set( i, j, matrix.get(i, j) );

		m2.set( attributes.size() - 1, attributes.size() - 1, 1.0d );
		matrix = m2;
		fireTableStructureChanged();
	}
	


	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int arg0) {
		return Double.class;
	}


	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return matrix.columns();
	}


	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int arg0) {
		return attributes.get( arg0 );
	}


	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return matrix.rows();
	}


	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int arg0, int arg1) {
		return new Double( matrix.get( arg0, arg1 ) );
	}


	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return arg0 <= arg1;
	}


	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		matrix.set( arg1, arg2, new Double( arg0.toString() ) );
		matrix.set( arg2, arg1, new Double( arg0.toString() ) );
		this.fireTableRowsUpdated( arg1, arg1 );
		this.fireTableCellUpdated( arg2, arg2 );
		CholeskyDecomposition cholesky = new CholeskyDecomposition( matrix );
		
		
		
		System.out.println( "cholesky.is symm+pos def? " + cholesky.isSymmetricPositiveDefinite() );
		System.out.println("L:\n" + cholesky.getL() );
	}
}