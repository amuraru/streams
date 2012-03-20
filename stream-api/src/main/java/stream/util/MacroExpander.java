/**
 * 
 */
package stream.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class performs a simple macro-expansion based on a pattern string and
 * a ModSecurity audit-log event.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class MacroExpander {

    /* A global logger for this class */
    static Logger log = LoggerFactory.getLogger( MacroExpander.class );

    public final static String VAR_PREFIX = "%{";
    public final static String VAR_SUFFIX = "}";

    /* The variables available in this context */
    Map<String,String> variables = new HashMap<String,String>();


    public MacroExpander(){
        this( new HashMap<String,String>() );
    }

    
    public MacroExpander( Map<String,? extends Serializable> vars ){
    	variables.clear();
    	for( String key : vars.keySet() ){
    		variables.put( key, vars.get( key ).toString() );
    	}
    }
    
    
    public MacroExpander( Properties p ){
        this.variables = new HashMap<String,String>();
        for( Object k : p.keySet() )
            variables.put( k.toString(), p.getProperty( k.toString() ) );
    }


    public void addVariables( Map<String,String> vars ){
        for( String key : vars.keySet() )
            variables.put( key, vars.get( key ) );
    }


    public void set( String key, String val ){
        variables.put( key, val );
    }



    protected String substitute( String str, Map<String,? extends Serializable> evt ){
        String content = str;
        int start = content.indexOf( VAR_PREFIX, 0 );
        while( start >= 0 ){
            int end = content.indexOf( VAR_SUFFIX, start );
            if( end >= start + 2 ){
                String variable = content.substring( start + 2, end );
                log.debug( "Found variable: {}", variable );
                log.trace( "   content is: {}", content );
                String val = get( variable, evt );
                if( val != null )
                    content = content.substring( 0, start ) + val + content.substring( end + 1 );
                else
                    content = content.substring( 0, start ) + "" + content.substring( end + 1 );

                if( end < content.length() )
                    start = content.indexOf( VAR_PREFIX, end );
                else
                    start = -1;
            } else
                start = -1;
        }
        return content;
    }


    public String get( String variable, Map<String,? extends Serializable> evt ){
        if( evt != null ){
            Serializable str = evt.get( variable );
            if( str != null )
                return str.toString();
        }
        return variables.get( variable );
    }
    
    
    public String expand( String str ){
    	return substitute( str, this.variables );
    }
    
    public static String expand( String string, Map<String,? extends Serializable> vars ){
        MacroExpander expander = new MacroExpander( vars );
        return expander.substitute( string, null );
    }
}