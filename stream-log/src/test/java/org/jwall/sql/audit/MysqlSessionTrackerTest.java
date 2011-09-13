package org.jwall.sql.audit;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.tree.TreeEdges;
import stream.io.DataStream;
import stream.io.DataStreamProcessor;
import stream.io.LogFileDataStream;
import stream.io.SyslogDataStream;

public class MysqlSessionTrackerTest
{
    static Logger log = LoggerFactory.getLogger( MysqlSessionTrackerTest.class );
    

    /**
     * @param args
     */
    public static void main(String[] args)
        throws Exception
    {
        URL url = MysqlSessionTrackerTest.class.getResource( "/mysql.log" );
        url = new URL( "file:/Users/chris/shop-database.log" );
        DataProcessor p = new MySQLSessionTracker();
        DataStream stream = new LogFileDataStream( url );
        stream = new SyslogDataStream( url );
        DataStreamProcessor dsp = new DataStreamProcessor( stream );
        dsp.addDataProcessor( p );
        
        SQLStreamParser sqlParser = new SQLStreamParser( "mysql:query" );
        sqlParser.getFixes().put( "SQL_CALC_FOUND_ROWS", "" );
        sqlParser.setErrorStream( new FileOutputStream( "/Users/chris/shop-sql-parser.err" ) );
        dsp.addDataProcessor( sqlParser );
        dsp.addDataProcessor( new TreeEdges() );

        PrintStream out = new PrintStream( new FileOutputStream( "/Users/chris/shop-db-trees.tsv" ) );
        
        Long count = 0L;
        Data item = dsp.readNext();
        
        String[] outKeys = new String[]{
                "mysql:timestamp", "mysql:database", "@tree:mysql:query"
        };
        
        while( item != null ){
            count++;
        
            p.process( item );
            
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
            item = dsp.readNext();
        }
        
        log.info( "Successfully parsed {} out of {} items", sqlParser.getSuccessCount(), sqlParser.getSuccessCount() + sqlParser.getErrorCount() );
        log.info( "{} items could not be parsed.", sqlParser.getErrorCount() );
        log.info( "   Success rate is {}", sqlParser.getSuccessCount().doubleValue() / sqlParser.getTotalCount().doubleValue() );
    }
}