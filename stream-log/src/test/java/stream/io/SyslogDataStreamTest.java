package stream.io;

import java.io.Serializable;
import java.net.URL;

import org.jwall.sql.audit.SQLStreamParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.tree.TreeEdges;

public class SyslogDataStreamTest
{
    static Logger log = LoggerFactory.getLogger( SyslogDataStreamTest.class );
    
    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        URL url = SyslogDataStreamTest.class.getResource( "/query.log" );
        SyslogDataStream stream = new SyslogDataStream( url );

        DataStreamProcessor proc = new DataStreamProcessor( stream );
        
        proc.addDataProcessor( new DataProcessor(){
            @Override
            public Data process(Data data) {
                
                Serializable msg = data.get( "MESSAGE" );
                if( msg != null && msg.toString().indexOf( "Query" ) > 0 ){
                    String txt = msg.toString();
                    int idx = txt.indexOf( "Query" );
                    log.info( "query: {}", txt.substring( idx + "Query".length() + 1 ) );
                    data.put( "sql", txt.substring( idx + "Query".length() + 1 ).trim() );
                }
                
                return data;
            }
        });
        
        //proc.addDataProcessor( new IngresSQLParser() );
        proc.addDataProcessor( new SQLStreamParser() );
        proc.addDataProcessor( new TreeEdges() );
        
        int i = 5;
        Data item = proc.readNext();
        while( i-- > 0 && item != null ){
            log.info( "Item[{}]: {}", i, item );
            
            for( String key : item.keySet() ){
                log.info( "  {} = {}", key, item.get(key) );
            }
            
            log.info( "item: {}", item );
            item = proc.readNext();
        }
    }
}