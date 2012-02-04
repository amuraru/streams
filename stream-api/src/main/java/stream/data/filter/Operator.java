package stream.data.filter;

import java.io.Serializable;


/**
 * <p>
 * An enumeration of available operators.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public enum Operator 
	implements Serializable
{

	EQ( "@eq"), NEQ( "!@eq" ), 
	GE( "@ge"), NGE( "!@ge" ),
	GT( "@gt"), NGT( "!@gt" ),
	LE( "@le"), NLE( "!@le" ),
	LT( "@lt"), NLT( "!@lt" ),
	PM( "@pm"), NPM( "!@pm" ),
	RX( "@rx"), NRX( "!@rx" ),
	SX( "@sx"), NSX( "!@sx" ),
	IN( "@in"), NIN( "!@in" ),
	Contains( "@contains"), NContains( "!@contains" ),
	BeginsWith( "@beginsWith"), NBeginsWith( "!@beginsWith" ),
	EndsWith( "@endsWith"), NEndsWith( "!@endsWith" );
	
	
	private final String name;
	
	Operator( String str ){
		this.name = str;
	}
	
	public String toString(){
		return name;
	}
	
	
	public static Operator read( String str ) throws FilterException {
		
		for( Operator op : values() )
			if( op.name.equals( str ) )
				return op;
		
		if( "=".equals( str ) || "==".equals( str ) )
			return EQ;
		
		if( "!=".equals( str ) || "<>".equals( str ) )
			return NEQ;
		
		if( "<=".equals( str ) )
			return LE;
		
		if( "<".equals( str ) )
			return LT;
		
		if( ">=".equals( str ) )
			return GE;
		
		if( ">".equals( str ) )
			return GT;
		
		throw new FilterException( "Invalid operator name: '" + str + "'!" );
	}
}