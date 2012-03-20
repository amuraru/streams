/**
 * 
 */
package stream.data.filter;

import java.io.Serializable;

/**
 * <p>
 * This class provides an abstract binary operator that is provided with 
 * two operands (one serializable, one string). The first operand is extracted
 * from the data item, the second operand is the string provided to this
 * operator in the filter expression.
 * </p>
 * <p>
 * For example, the following filter expression includes a single binary
 * operator that obtains the value for feature "x" (serializable) and the 
 * user given value "3" as a String:
 * </p>
 * <pre>
 *      x @gt 3
 * </pre>
 * 
 * It is the operator's implementation responsibility to interpret the String
 * value accordingly.
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public abstract class BinaryOperator extends Operator {

	/** The unique class ID */
	private static final long serialVersionUID = -2092261987796548990L;
	
	/**
	 * @param str
	 */
	public BinaryOperator(String str) {
		super(str);
	}
	
	public BinaryOperator( String str, String ... alias ){
		super( str, alias );
	}
	

	public boolean isNumeric( Serializable val ){
		
		if( val instanceof Double ){
			return true;
		}
		
		if( val == null )
			return false;
		
		try {
			new Double( val.toString() );
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param featureValue
	 * @param value
	 * @return
	 */
	public abstract boolean eval( Serializable featureValue, String value );
}