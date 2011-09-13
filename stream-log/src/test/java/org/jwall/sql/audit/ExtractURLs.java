package org.jwall.sql.audit;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;

import org.jwall.web.audit.HttpRequestTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.tree.TreeEdges;
import stream.io.AbstractDataStream;
import stream.io.LogFileDataStream;
import stream.io.LogStreamParser;
import stream.io.ModSecurityAuditStream;

public class ExtractURLs
{
    static Logger log = LoggerFactory.getLogger( ExtractURLs.class );

    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        AbstractDataStream stream = new LogFileDataStream( new URL( "file:/Users/chris/shop-access.log" ) );
        
        stream = new ModSecurityAuditStream( new URL( "file:/Users/chris/shop-audit.log" ) );
        
        LogStreamParser parser = new LogStreamParser();
        parser.setFormat( "%{REMOTE_ADDR} %{REMOTE_USER} %{REMOTE_AUTH} %{DATE|\\[.*\\]} \"%{REQUEST_METHOD} %{REQUEST_URI} %{REQUEST_PROTO}\" %{RESPONSE_STATUS} %{RESPONSE_SIZE} \"%{REQUEST_HEADERS:REFERER}\" \"%{REQUEST_HEADERS:USER-AGENT}\"" );
        stream.addPreprocessor( parser );
        stream.addPreprocessor( new DateParser( "DATE", "[dd/MMM/yyyy:HH:mm:ss Z]" ) );
        stream.addPreprocessor( new DataProcessor(){
            @Override
            public Data process(Data data)
            {
                if( data.containsKey( "REQUEST_URI" ) ){
                    String uri = data.get( "REQUEST_URI" ).toString();
                    int idx = uri.indexOf( "?" );
                    if( idx > 0 ){
                        String path = uri.substring( 0, idx );
                        data.put( "REQUEST_PATH", path );
                        String qs = uri.substring( idx + 1 );
                        
                        String[] kv = qs.split( "&" );
                        for( String pv : kv ){
                            String[] tok = pv.split("=");
                            if( tok.length == 2 ){
                                data.put( "ARGS:" + tok[0], tok[1] );
                            }
                        }
                    } else
                        data.put( "REQUEST_PATH", uri );
                }
                return data;
            }
        });
        stream.addPreprocessor( new HttpRequestTree() );
        stream.addPreprocessor( new TreeEdges() );
        
        Integer limit = Integer.MAX_VALUE;
        Data item = stream.readNext();
        String[] out = new String[]{ "TIMESTAMP", "REMOTE_ADDR", "RESPONSE_STATUS", "REQUEST_PATH", "@tree:request" };
        PrintStream p = new PrintStream( new FileOutputStream( "/Users/chris/shop-requests.log" ) );
        //limit = 100;
        int i = 1;
        while( item != null && limit-- > 0 ){
            log.info( "item[{}]", i++ );
            for( String key : item.keySet() ){
                //log.info( "   {} = {}", key, item.get( key ) );
            }
            /*
            Long ts = new Long( item.get( "TIMESTAMP" ).toString() );
            Date date = new Date( ts );
             */
            
            StringBuffer s = new StringBuffer();
            for( String key : out ){
                if( item.get( key ) != null ){
                    s.append( item.get( key ).toString() );
                }
                s.append( "\t" );
            }
            
            p.println( s.toString() );
            //log.info( "access at: {}", date );
            item = stream.readNext();
        }
    }
}