/**
 * 
 */
package stream.data.filter;

import stream.data.Data;

/**
 * @author chris
 *
 */
public class Match 
	implements FilterExpression
{
	/** The unique class ID */
	private static final long serialVersionUID = 7007162167342940123L;
	
	String variable;
	Operator op;
	String value;
	
	
	public Match( String variable, Operator o, String value ){
		this.variable = variable;
		this.op = o;
		this.value = value;
	}
	

	public boolean matches( Data item ){
		return false;
	}
}
