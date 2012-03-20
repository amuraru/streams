/**
 * 
 */
package stream.util;

/**
 * @author chris
 *
 */
public class ParameterUtils {

	
	public final static String[] split( String keys ){
		return split( keys, "," );
	}
	
	
	public final static String[] split( String keys, String separator ){
		
		if( keys == null || keys.isEmpty() )
			return new String[0];
		
		String[] elements = keys.split( separator );

		int cnt = 0;
		for( int i = 0; i < elements.length; i++ ){
			String val = elements[i].trim();
			if( !val.isEmpty() )
				cnt++;
		}
		
		
		String[] out = new String[ cnt ];
		cnt = 0;
		for( int i = 0; i < elements.length; i++ ){
			if( !elements[i].isEmpty() ){
				out[cnt++] = elements[i];
			}
		}
		
		return out;
	}
	
	
	public final static String join( String[] keys ){
		
		if( keys == null )
			return null;
		
		StringBuffer s = new StringBuffer();
		int cnt = 0;
		
		for( String key : keys ){
			if( ! key.trim().isEmpty() ){
				if( cnt > 0 )
					s.append( "," );
				cnt++;
			}
		}
		
		return s.toString();
	}
}