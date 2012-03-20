/**
 * 
 */
package stream.data.filter;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;

/**
 * @author chris
 *
 */
public class Match 
	implements Expression
{
	static Logger log = LoggerFactory.getLogger( Match.class );
	
	/** The unique class ID */
	private static final long serialVersionUID = 7007162167342940123L;
	
	String variable;
	Operator op;
	String value;
	boolean negated = false;
	
	public Match( String variable, Operator o, String value ){
		this.variable = variable;
		this.op = o;
		this.value = value;
		this.negated = o.isNegated();
	}
	

	public boolean matches( Data item ){
		
		Serializable featureValue = item.get( variable );
		if( op instanceof BinaryOperator ){
			BinaryOperator binOp = (BinaryOperator) op;
			boolean match = binOp.eval( featureValue, this.value );
			
			boolean result = match;
			if( negated ){
				result = !match;
			} 
			
			return result;
		}
		
		throw new RuntimeException( "Unsupported non-binary operator: " + op );
	}
}
