package stream.data.filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionCompiler {
	
	
	static Logger log = LoggerFactory.getLogger( ExpressionCompiler.class );

	

	public final static Expression parse( String str ) throws ExpressionException {
		log.debug( "Parsing expression: '{}'", str );
		if( str == null || str.trim().isEmpty() )
			return null;
		
		ExpressionReader r = new ExpressionReader( str );
		return r.readFilterExpression();
	}

	public final static Expression parse( String str, Collection<String> variables ) throws ExpressionException {
		log.debug( "Parsing expression: '{}'", str );
		ExpressionReader r = new ExpressionReader( str, variables );
		return r.readFilterExpression();
	}
	
	public static List<Expression> expand( ExpressionList list ){
		List<Expression> exp = new LinkedList<Expression>();
		
		for( Expression e : list.getElements() ){
			if( e instanceof ExpressionList )
				exp.addAll( expand( (ExpressionList) e ) );
			else
				exp.add( e );
		}
		
		return exp;
	}
	
	
	/*
	public final static Match map( FilterExpression e ){
		if( e instanceof Match ){
			return (Match) e;
		}
		
		if( e instanceof AuditEventMatch ){
			AuditEventMatch aem = (AuditEventMatch) e;
			return new Match( aem.getVariable(), aem.getOp(), aem.getValue() );
		}
		
		return null;
	}
	
	
	public final static String toFilterString( Object e ){
		if( e == null ){
		        return "null";
		}

		StringBuffer s = new StringBuffer();
		if( e instanceof AuditEventFilter ){
			AuditEventFilter aef = (AuditEventFilter) e;
			Iterator<AuditEventMatch> it = aef.getMatches().iterator();
			while( it.hasNext() ){
				AuditEventMatch match = it.next();
				s.append( match.getVariable() );
				s.append( " " );
				s.append( match.getOperator() );
				s.append( " " );
				s.append( match.getValueObject() );
				if( it.hasNext() ){
					s.append( " AND " );
				}
			}
			
		} else
			s.append( e.toString() );
		
		return s.toString();
	}
	 */
}
