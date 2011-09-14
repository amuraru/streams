package stream.io;

import java.util.Map;

import org.jwall.log.io.ParserGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;

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