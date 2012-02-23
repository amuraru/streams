package stream.io;

import java.util.Map;

import org.jwall.log.io.ParserGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.Description;


/**
 * <p>
 * This processor implements a parser that can parse the String contents of a
 * specific key into its parts. The parser is generic and derived from a "format"
 * string.
 * </p>
 * <p>
 * By default it uses the key <code>MESSAGE</code> as the input key.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Description( group="Data Stream.Sources", text= "")
public class LogStreamParser
    implements DataProcessor
{
    static Logger log = LoggerFactory.getLogger( LogStreamParser.class );
    String key = "MESSAGE";
    
    String format = "";
    org.jwall.log.io.Parser<Map<String,String>> parser;
    
    
    public LogStreamParser(){
        parser = null;
    }
    
    public LogStreamParser( String fmt ){
        setFormat( fmt );
    }
    
    
    public void setFormat( String fmt ){
        this.format = fmt;
        ParserGenerator pg = new ParserGenerator( fmt );
        parser = pg.newParser();
    }
    
    
    public String getFormat(){
        return format;
    }
    
    /**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
     * @see stream.data.DataProcessor#process(stream.data.Data)
     */
    @Override
    public Data process(Data data) {
        
        if( ! data.containsKey( key ) || parser == null )
            return data;
        
        String msg = data.get( key ).toString();
        try {
            Map<String,String> map = parser.parse( msg );
            for( String k : map.keySet() ){
                data.put( k, map.get( k ) );
            }
        } catch (Exception e) {
            log.error( "Failed to parse message '{}': {}", msg, e.getMessage() );
            e.printStackTrace();
        }
        return data;
    }
}