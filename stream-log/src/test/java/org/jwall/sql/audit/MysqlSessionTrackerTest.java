package org.jwall.sql.audit;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.DataProcessor;
import stream.data.mapper.Mapper;
import stream.data.tree.TreeEdges;
import stream.io.DataStream;
import stream.io.DataStreamProcessor;
import stream.io.DataStreamWriter;
import stream.io.LogFileDataStream;
import stream.io.SyslogDataStream;

public class MysqlSessionTrackerTest
{
    final static Logger log = LoggerFactory.getLogger( MysqlSessionTrackerTest.class );
    

    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        URL url = MysqlSessionTrackerTest.class.getResource( "/mysql.log" );
        url = new URL( "file:/Users/chris/sql-mining/query.log" );
        DataProcessor p = new MySQLSessionTracker();
        DataStream stream = new LogFileDataStream( url );
        stream = new SyslogDataStream( url );
        DataStreamProcessor dsp = new DataStreamProcessor( stream );
        dsp.addDataProcessor( p );
        dsp.addDataProcessor( new DataProcessor(){
            @Override
            public Data process(Data data){
                /*
                for( String key : data.keySet() ){
                    log.info( "  {} = {}", key, data.get( key ) );
                }
                 */
                
                String con = data.get( "mysql:connection" ) + "";
                if( con.indexOf( "shop" ) < 0 ){
                    log.info( "Clearing data {}", data );
                    return new DataImpl();
                }
                
                return data;
            }
            
        });
        SQLStreamParser sqlParser = new SQLStreamParser( "mysql:query" );
        sqlParser.addMapper( new Mapper<String,String>(){

            @Override
            public String map(String input) throws Exception {
                if( input.endsWith( "#" ) ){
                    String str = input.substring( 0, input.length() - 1 );
                    //log.info( "Mapping:\n\t{}\nto {}", input, str );
                    return str;
                }
                return input;
            }
        });
        sqlParser.getFixes().put( "SQL_CALC_FOUND_ROWS", "" );
        sqlParser.getFixes().put( " AS CHAR)", ")");
        sqlParser.getFixes().put( "NULL#", "NULL" );
        sqlParser.getFixes().put( ")#", ")" );
        sqlParser.getFixes().put( " left(", " substring(" );
        sqlParser.getFixes().put( "information_schema.SCHEMATA#", "information_schema.SCHEMATA" );
        sqlParser.setErrorStream( new FileOutputStream( "/Users/chris/sql-mining/shop-sql-parser.err" ) );
        dsp.addDataProcessor( sqlParser );
        dsp.addDataProcessor( new TreeEdges() );

        PrintStream out = new PrintStream( new FileOutputStream( "/Users/chris/test-queries2.tsv" ) );
        DataStreamWriter dsw = new DataStreamWriter( new FileOutputStream( "/Users/chris/sql-mining/test-sql.tsv" ), "\t" );
        dsw.setKeys( "mysql:timestamp,mysql:session,mysql:database,@tree:mysql:query" );
        Long count = 0L;
        Data item = dsp.readNext();
        
        String[] outKeys = new String[]{
                "mysql:timestamp", "mysql:database", "mysql:query", "@tree:mysql:query"
        };
        int written = 0;
        int i = 0;
        while( item != null && i++ < 100000 ){
        
            p.process( item );
            if( item.get( "mysql:query" ) != null )
                count++;
            
            int ok = 0;
            StringBuffer s = new StringBuffer();
            for( String key : outKeys ){
                Serializable val = item.get( key );
                if( val != null ){
                    s.append( val.toString() );
                    ok++;
                }
                s.append( "\t" );
            }
            if( ok == outKeys.length )
                out.println( s.toString() );
            //for( String key : item.keySet() ){
            //    log.info( "   {} = {}", key, item.get( key ) );
            //}
            if( item.get( "mysql:query" ) != null && "appdb".equals( item.get( "mysql:database" ) ) && item.get( "@tree:mysql:query" ) != null ){
                dsw.dataArrived( item );
                written++;
            } else {
                //log.info( "Skipping item with mysql:database={}", item.get( "mysql:database" ) );
            }
            item = dsp.readNext();
        }
        log.info( "{} items written", written );
        log.info( "Successfully parsed {} out of {} items", sqlParser.getSuccessCount(), sqlParser.getSuccessCount() + sqlParser.getErrorCount() );
        log.info( "{} items could not be parsed.", sqlParser.getErrorCount() );
        log.info( "   Success rate is {}", sqlParser.getSuccessCount().doubleValue() / sqlParser.getTotalCount().doubleValue() );
    }
}