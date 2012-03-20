package stream.data.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionReader {

	int pos = 0;
	String input;
	Set<Operator> supportedOperators = new HashSet<Operator>();
	Set<BooleanOperator> supportedBoolOperators = new HashSet<BooleanOperator>();
	Set<String> supportedVariables = null;
	
	public final static boolean STRICT_PARSING = "true".equalsIgnoreCase( "" + System.getProperty( "event.filter.strict" ) );
	static Logger log = LoggerFactory.getLogger( ExpressionReader.class );
	boolean strictParsing = STRICT_PARSING;

	protected ExpressionReader( String str ){
		input = str;
		pos = 0;
		supportedBoolOperators.add( BooleanOperator.AND );
		supportedBoolOperators.add( BooleanOperator.OR );
		
		for( Operator op : Operator.OPERATORS.values() )
			supportedOperators.add( op );
	}

	
	protected ExpressionReader( String str, Collection<String> variables ){
		this( str );
		supportedVariables = new HashSet<String>();
		for( String var : variables )
			supportedVariables.add( var.toUpperCase() );
	}
	
	
	protected ExpressionReader( String str, Collection<String> variables, Set<Operator> operators ){
		this( str, variables );
		supportedOperators.clear();
		supportedOperators.addAll( operators );
	}

	
	
	/**
	 * @return the strictParsing
	 */
	public boolean isStrictParsing() {
		return strictParsing;
	}


	/**
	 * @param strictParsing the strictParsing to set
	 */
	public void setStrictParsing(boolean strictParsing) {
		this.strictParsing = strictParsing;
	}


	public void setBooleanOperators( Set<BooleanOperator> booleanOps ){
		supportedBoolOperators = new HashSet<BooleanOperator>( booleanOps );
	}
	
	public Set<BooleanOperator> getBooleanOperators(){
		return supportedBoolOperators;
	}

	protected Expression readFilterExpression() throws ExpressionException {

		skipWhiteSpace();
		if( startsWith( "(") ){
			log.debug( "Found nested expression at pos {}: {}", pos, input.substring(pos) );
			ExpressionList list =  this.readNestedExpression();
			//if( list.size() == 1 )
			//	return list.getFirst();
			log.debug( "\nnested expression: {}\n\n", list.toString() );
			return list;

		} else {

			Expression first = readSimpleFilter();
			if( ! endOfLine() && !hasBooleanOperator() && ! startsWith( ")" ) )
				throw new ExpressionException( "Boolean operator 'AND' or 'OR' expected after '" + input.substring( 0, pos ) + "'!");

			if( endOfLine() )
				return first;

			List<Expression> exps = new ArrayList<Expression>();
			List<BooleanOperator> ops = new ArrayList<BooleanOperator>();
			exps.add(first);
			while( !endOfLine() && hasBooleanOperator() ){
				ops.add( readBooleanOperator() );
				if( startsWith( "(" ) )
					exps.add( readNestedExpression() );
				else
					exps.add( readSimpleFilter() );
			}
			log.debug( "Ops: {}", ops );
			log.debug( "Exps: {}", exps );

			if( exps.size() == 1 )
				return first;

			return new ExpressionList( ops.iterator().next(), exps );
		}
	}

	public boolean endOfLine(){
		return pos >= input.length() || input.substring(pos).trim().equals( "" );
	}

	public ExpressionList readNestedExpression() throws ExpressionException {
		if( ! startsWith( "(" ) )
			throw new ExpressionException( "No start of nested expression found!" );

		skipWhiteSpace();
		pos++;

		Collection<Expression> exp = new ArrayList<Expression>();
		exp.add( readFilterExpression() );
		BooleanOperator op = null;
		while( !startsWith( ")" ) ){

			if( endOfLine() )
				throw new ExpressionException( "Unexpected end of expression! Missing a closing bracket?" );

			op = readBooleanOperator();

			if( startsWith( "(" ) )
				exp.add( readNestedExpression() );
			else
				exp.add( readFilterExpression() );
		}
		pos++;
		return new ExpressionList( op, exp );
	}


	public boolean startsWith( String str ){
		int i = pos;
		while( i < input.length() && Character.isWhitespace( input.charAt(i) ) )
			i++;

		if( i >= input.length() )
			return false;

		return input.substring( i ).startsWith( str );
	}


	public Expression readSimpleFilter() throws ExpressionException {
		try {
			String var = readVariable();
			Operator op = readOperator();
			String value = readValue();
			return new Match( var, op, value );
		} catch (ExpressionException se) {
			throw new ExpressionException( se.getMessage() );
		}
	}


	protected boolean hasBooleanOperator(){
		int i = pos;
		while( i < input.length() && Character.isWhitespace( input.charAt(i) ) )
			i++;

		if( i >= input.length() )
			return false;

		String rest = input.substring( i );
		return rest.toLowerCase().startsWith( "or " ) || rest.toLowerCase().startsWith( "and " );
	}


	protected BooleanOperator readBooleanOperator() throws ExpressionException {
		skipWhiteSpace();
		
		StringBuffer var = new StringBuffer();
		skipWhiteSpace();
		while( pos < input.length() && ! Character.isWhitespace( input.charAt( pos ) ) )
			var.append( input.charAt( pos++ ) );

		String operatorName = var.toString();
		BooleanOperator op = BooleanOperator.read( operatorName );
		if ( ! supportedBoolOperators.contains( op ) ){
			if( this.isStrictParsing() )
				throw new ExpressionException( "Boolean operator '" + operatorName + "' is not supported!" );
			else
				log.warn( "Boolean operator '{}' is not supported, but 'strictParsing' is disabled!", operatorName );
		}
		return op;
	}


	public int getPosition(){
		return pos;
	}

	public String getInputString(){
		return input;
	}


	protected void skipWhiteSpace( ){
		while( pos < input.length() && Character.isWhitespace( input.charAt( pos ) ) )
			pos++;
	}

	protected String readVariable() throws ExpressionException {
		StringBuffer var = new StringBuffer();
		skipWhiteSpace();
		while( pos < input.length() && ! Character.isWhitespace( input.charAt( pos ) ) )
			var.append( input.charAt( pos++ ) );

		String variable = var.toString();
		
		if( this.supportedVariables != null && ! supportedVariables.contains( variable.toUpperCase() ) ){
			if( isStrictParsing() )
				throw new ExpressionException( "Not a valid variable name '" + variable + "'!" );
			else
				log.warn( "Found variable '{}' which has not been defined previously!", variable );
		}
		
		return variable;
	}

	protected Operator readOperator() throws ExpressionException {

		skipWhiteSpace();
		if( pos >= input.length() )
			throw new ExpressionException( "Operator expected at position " + pos + ", found: '" + input.substring( pos ) + "'!" );

		StringBuffer buf = new StringBuffer();
		while( pos < input.length() && ! Character.isWhitespace( input.charAt( pos ) ) )
			buf.append( input.charAt( pos++ ) );

		//
		// extract the appropriate operator, this will throw an exception
		// if the operator name is not valid
		//
		Operator op = Operator.read( buf.toString() );
		if( !supportedOperators.contains( op ) ){
			if( isStrictParsing() )
				throw new ExpressionException( "Operator '" + op.toString() + "' not supported!" );
			else
				log.warn( "Operator '{}' is not supported, but 'strictParsing' disabled!", op );
		}
		
		return op;
	}

	protected String readValue() throws ExpressionException {
		StringBuffer var = new StringBuffer();
		skipWhiteSpace();

		if( endOfLine() || startsWith( ")" ) || hasBooleanOperator() )
			throw new ExpressionException( "Value expected at position " + pos + "!" );

		if( input.charAt( pos ) == '"' || input.charAt( pos ) == '\'' ){
			return readQuotedString( input.charAt( pos ) );
		}

		while( pos < input.length() && input.charAt( pos ) != ')' && ! Character.isWhitespace( input.charAt( pos ) ) )
			var.append( input.charAt( pos++ ) );

		return var.toString();
	}



	protected String readQuotedString( char ch ){
		StringBuffer s = new StringBuffer();
		while( pos < input.length() && ch != input.charAt( pos ) ){
			pos++;
		}
		pos++;

		if( pos < input.length() )
			s.append( input.charAt( pos++ ) );

		char last = ' ';
		while( pos < input.length() && last != '\\' && ch != input.charAt( pos ) ){
			char cur = input.charAt( pos++ );
			s.append( cur );
			last = cur;
		}

		pos++;
		return s.toString();
	}


	public final static Expression parse( String str ) throws ExpressionException {
		log.debug( "Parsing expression: {}", str );
		ExpressionReader r = new ExpressionReader( str );
		return r.readFilterExpression();
	}
}