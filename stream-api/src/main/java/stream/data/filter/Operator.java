package stream.data.filter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * <p>
 * An enumeration of available operators.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public abstract class Operator 
	implements Serializable
{
	/** The unique class ID  */
	private static final long serialVersionUID = 5150175070404610787L;
	
	public final static Operator EQ = new ConditionEQ();
	public final static Operator LT = new ConditionLT();
	public final static Operator LE = new ConditionLE();
	public final static Operator GT = new ConditionGT();
	public final static Operator GE = new ConditionGE();
	public final static Operator PM = new ConditionPM();
	public final static Operator RX = new ConditionRX();
	public final static Operator SX = new ConditionSX();
	
	public final static Map<String,Operator> OPERATORS = new LinkedHashMap<String,Operator>();
	
	static {
		registerOperator( EQ );
		registerOperator( LT );
		registerOperator( LE );
		registerOperator( GT );
		registerOperator( GE );
		registerOperator( PM );
		registerOperator( RX );
		registerOperator( SX );
	}
	
	
	public final static void registerOperator( Operator op ){
		OPERATORS.put( op.name, op );
		OPERATORS.put( "!" + op.name, op );
		for( String alias : op.getAliases() ){
			registerAlias( op, alias );
		}
	}
	
	
	public final static void registerAlias( Operator op, String alias ){
		if( OPERATORS.get( op.name ) != null ){
			OPERATORS.put( alias, OPERATORS.get( op.name ) );
		}
	}
	
	
	
	private final String name;
	private final String[] aliases;
	
	public Operator( String str ){
		this( str, new String[0] );
	}
	
	public Operator( String str, String[] aliases ){
		this.name = str;
		this.aliases = aliases;
	}
	
	public boolean isNegated(){
		return this.name.startsWith( "!" );
	}
	
	public String toString(){
		return name;
	}
	
	
	public String[] getAliases(){
		return aliases;
	}
	
	
	
	public static Operator read( String str ) throws ExpressionException {
		
		for( Operator op : OPERATORS.values() ){
			if( op.name.equals( str ) ){
				return op;
			}
		}
		
		throw new ExpressionException( "Invalid operator name: '" + str + "'!" );
	}
}