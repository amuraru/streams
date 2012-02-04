package stream.data.filter;

public enum BooleanOperator {
	OR, AND;
	
	public static BooleanOperator read( String str ) throws FilterException {
		if( str.equalsIgnoreCase( "and" ) )
			return AND;
		else
			return OR;
	}
}
