package stream.tools;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.io.DataStreamWriter;
import stream.util.Base64Codec;

public class StreamLogger
{
    static Logger log = LoggerFactory.getLogger( StreamLogger.class );
    public final static String separator = "|";
    URL url;
    DataStreamWriter out;
    Data item = new DataImpl();
    String basicAuth = null;

    public StreamLogger( DataStreamWriter out ){
        this.out = out;
    }

    public StreamLogger( OutputStream out ){
        this.out = new DataStreamWriter( out, separator );
    }
    
    public StreamLogger( URL url ){
        this.url = url;
    }

    public StreamLogger( URL url, String user, String password ){
        this.url = url;
        if( user != null && password != null )
            basicAuth = "Basic " + Base64Codec.encode( (user + ":" + password) );
    }

    public void log( String msg ){
        log( 0, msg );
    }

    public void log( Integer level, String msg ){
        Data item = new DataImpl();
        item.put( "LEVEL", "" + level );
        item.put( "MESSAGE", msg );
        log( item );
    }

    public void log( Data item ){
        if( !item.containsKey( "@timestamp" ) )
            item.put( "@timestamp", System.currentTimeMillis() );
        send( item );
    }

    public void log( Map<String,String> msg ){
        item.clear();
        item.put( "@timestamp", System.currentTimeMillis() );
        item.putAll( msg );
        send( item );
    }

    public void send( Data item ){

        if( url != null ){
            log.debug( "Trying to send data item to URL {}", url );
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod( "PUT" );
                
                if( basicAuth != null ){
                    log.debug( "Using basic authentication..." );
                    con.setRequestProperty( "authorization", basicAuth );
                }
                
                con.setDoInput( false );
                con.setDoOutput( true );
                DataStreamWriter w = new DataStreamWriter( con.getOutputStream(), separator );
                w.process( item );
                w.close();

                String response = URLUtilities.readResponse( con.getInputStream() );
                log.debug( "Data send, response: {}", response );
            } catch (Exception e) {
                log.error( "Failed to send log: {}", e.getMessage() );
                if( log.isTraceEnabled() )
                    e.printStackTrace();
            }
        }

        if( out != null ){
            log.debug( "Writing data item to {}", out );
            out.process( item );
        }
    }
}