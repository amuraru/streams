/**
 * 
 */
package stream.web.layout;

import java.util.Map;

/**
 * @author chris
 *
 */
public abstract class MarkdownPreprocessor {
	int pos = -1;
	String startTag = "$$";
	String endTag = "$$";

	
	public MarkdownPreprocessor( String startTag, String endTag ){
		this.startTag = startTag;
		this.endTag = endTag;
	}
	
	public String process( String source, final Map<String,String> subs ){
		
		pos = findStart( source );
		while( pos >= 0 ){
	
			int end = findEnd( source );
			if( end > pos ){
				String input = source.substring( pos + startTag.length(), end );
				Substitution sub = compile( input, startTag, endTag );
				if( sub != null ){
					subs.put( sub.ref, sub.txt );
					source = source.substring( 0, pos ) + sub.txt + source.substring( end + endTag.length() );
				}
			}
			
			pos = findStart( source );
		}
		
		return source;
	}
	
	
	public abstract Substitution compile( String source, String start, String end );
	
	
	protected int findStart( String source ){
		if( pos < 0 )
			return source.indexOf( startTag );
		else
			return source.indexOf( startTag, pos );
	}
	
	protected int findEnd( String source ){
		if( pos < 0 )
			return source.indexOf( endTag );
		else
			return source.indexOf( endTag, pos );
	}
	
	public class Substitution {
		String ref;
		String txt;
		
		public Substitution( String ref, String content ){
			this.ref = ref;
			this.txt = content;
		}
	}
}