package org.jwall.sql.audit;

import java.io.FileOutputStream;
import java.net.URL;

import org.jwall.web.audit.HttpRequestTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.tree.TreeEdges;
import stream.io.AbstractDataStream;
import stream.io.DataStreamWriter;
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
        //stream.addPreprocessor( parser );
        //stream.addPreprocessor( new DateParser( "DATE", "[dd/MMM/yyyy:HH:mm:ss Z]" ) );
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
        
        DataStreamWriter writer = new DataStreamWriter( new FileOutputStream("/Users/chris/test-requests.tsv" ), "\t" );
        writer.setKeys( "TIMESTAMP,REQUEST_URI,REQUEST_HEADERS:USER-AGENT,@tree:request" );
        
        //limit = 100;
        int written = 0;
        while( item != null && limit-- > 0 ){
            if( written > 0 && written % 100 == 0 ){
                log.info( "{} items processed.", written );
            }
            String uri = "" + item.get( "REQUEST_URI" );
            if( uri.indexOf( "php" ) > 0 && uri.indexOf( "/blog/" ) < 0 ){
                writer.dataArrived( item );
                written++;
            }
            
            //log.info( "access at: {}", date );
            item = stream.readNext();
        }
        
        log.info( "{} items written.", written );
    }
}