package org.jwall.sql.audit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;

public class MySQLSessionTracker
    implements DataProcessor
{
    static Logger log = LoggerFactory.getLogger( MySQLSessionTracker.class );
    
    final static Pattern META_INF_PATTERN = Pattern.compile( "(\\d+)\\s(Init\\sDB|Query|Quit|Connect)" );

    SimpleDateFormat fmt = new SimpleDateFormat( "yyMMdd HH:mm:ss" );
    String key = "MESSAGE";
    String prefix = "mysql:";
    Long timestamp = 0L;
    
    Map<String,Session> sessions = new HashMap<String,Session>();
    
    StringBuffer buffer = new StringBuffer();
    
    public MySQLSessionTracker(){
        this( "MESSAGE" );
    }
    
    public MySQLSessionTracker( String msgKey ){
        key = msgKey;
    }
    

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }


    /**
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }


    /**
     * @return the sessions
     */
    public Map<String, Session> getSessions()
    {
        return sessions;
    }


    @Override
    public Data process(Data data) {
        if( key == null )
            return data;
        
        Serializable val = data.get( key );
        if( val == null )
            return data;
        
        String msg = val.toString();
        log.trace( "Checking message: {}", msg );
     
        /*
        if( this.isStatusLine( msg ) ){
            log.info( "CompletedBuffer:\n{}", buffer );
            data.put( prefix + "sql", buffer.toString() );
            data.putAll( this.createSession( buffer.toString() ) );
            buffer = new StringBuffer( msg );
            return data;
        } else {
            log.info( "Appending to buffer: {}", msg.trim() );
            buffer.append( msg );
        }
         */
        
        Matcher m = META_INF_PATTERN.matcher( msg );
        if( m.find() ){
            
            String match = msg.substring( m.start(), m.end() );
            
            if( m.start() > 0 ) {
                String pre = msg.substring( 0, m.start() ).trim();
                if( ! "".equals( pre ) ){
                    try {
                        Date date = this.fmt.parse( pre );
                        timestamp = date.getTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            log.trace( "  mysql:timestamp = {}", timestamp );
            
            String remain = msg.substring( m.end() );
            log.debug( "Found match: '{}'", match );
            String[] tok = match.split( " ", 2 );
            log.debug( "  mysql:session = {}", tok[0] );
            log.debug( "  mysql:action = {}", tok[1] );
            
            Session s = sessions.get( tok[0] );
            if( s == null ){
                s = new Session( tok[0] );
                sessions.put( tok[0], s );
            }
            
            if( "Connect".equals( tok[1] ) ){
                s = new Session( tok[0] );
                sessions.put( tok[0], s );
                s.put( prefix + "connection", remain.trim() );
                s.put( prefix + "connection:time", timestamp.toString() );
            }
            
            if( "Init DB".equals( tok[1] ) ){
                s.put( prefix + "database", remain.trim() );
            }
            
            if( "Query".equals( tok[1] ) ){
                String query = remain.trim();
                int i = query.indexOf( "ON DUPLICATE KEY UPDATE" );
                if( i > 0 )
                    query = query.substring( 0, i );
                
                s.put( prefix + "query", query );
                log.trace( "Found query: {}", query );
            }
            
            if( "Quit".equals( tok[1] ) ){
                s.remove( tok[0] );
            }
            
            for( String key : s.keySet() ){
                data.put( key, s.get( key ) );
            }
            
            data.put( prefix + "timestamp", timestamp.toString() );
        }
        
        return data;
    }
    
    
    public boolean isStatusLine( String line ){
        Matcher m = META_INF_PATTERN.matcher( line );
        return m.find();
    }
    
    
    public Map<String,String> createSession( String msg ){
        Map<String,String> data = new LinkedHashMap<String,String>();
        
        Matcher m = META_INF_PATTERN.matcher( msg );
        if( m.find() ){
            
            String match = msg.substring( m.start(), m.end() );
            
            if( m.start() > 0 ) {
                String pre = msg.substring( 0, m.start() ).trim();
                if( ! "".equals( pre ) ){
                    try {
                        Date date = this.fmt.parse( pre );
                        timestamp = date.getTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            log.trace( "  mysql:timestamp = {}", timestamp );
            
            String remain = msg.substring( m.end() );
            log.trace( "Found match: '{}'", match );
            String[] tok = match.split( " ", 2 );
            log.trace( "  mysql:session = {}", tok[0] );
            log.trace( "  mysql:action = {}", tok[1] );
            
            Session s = sessions.get( tok[0] );
            if( s == null ){
                s = new Session( tok[0] );
                sessions.put( tok[0], s );
            }
            
            if( "Connect".equals( tok[1] ) ){
                s = new Session( tok[0] );
                sessions.put( tok[0], s );
                s.put( prefix + "connection", remain.trim() );
                s.put( prefix + "connection:time", timestamp.toString() );
            }
            
            if( "Init DB".equals( tok[1] ) ){
                s.put( prefix + "database", remain.trim() );
            }
            
            if( "Query".equals( tok[1] ) ){
                s.put( prefix + "query", remain.trim() );
            }
            
            if( "Quit".equals( tok[1] ) ){
                s.remove( tok[0] );
            }
            
            for( String key : s.keySet() ){
                data.put( key, s.get( key ) );
            }
            
            data.put( prefix + "timestamp", timestamp.toString() );
        }
        
        return data;
    }
    
    
    public class Session extends LinkedHashMap<String,String> {
        /** The unique class ID */
        private static final long serialVersionUID = 317354859197243702L;
        String key;
        
        public Session( String id ){
            this.key = id;
            this.put( "mysql:session", id );
        }
    }
}