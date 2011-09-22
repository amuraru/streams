package org.jwall.sql.audit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import stream.data.Data;
import stream.io.DataStream;
import stream.io.DataStreamProcessor;
import stream.io.DataStreamWriter;
import stream.io.SyslogDataStream;


/**
 * This class implements a simple tool that will scan a datastream for
 * SQL data and output a tab-separated value file with a timestamp and
 * a parse-tree for each statement that has been successfully parsed
 * from the input file.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class TimestampedSQL
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            String file = "/Users/chris/shop-database.log";
            if( args.length > 0 )
                file = args[0];
            
            File outFile = new File( "/Users/chris/test-sql.out" );
            if( args.length > 1 )
                outFile = new File( args[1] );
            
            OutputStream out = new FileOutputStream( outFile );
            
            File in = new File( file );
            URL url = new URL( "file:" + in.getAbsolutePath() );
            
            DataStream stream = new SyslogDataStream( url );
            
            DataStreamProcessor dsp = new DataStreamProcessor( stream );
            dsp.addDataProcessor( new MySQLSessionTracker() );
            
            SQLStreamParser sqlParser = new SQLStreamParser( "mysql:query" );
            sqlParser.getFixes().put( "SQL_CALC_FOUND_ROWS", "" );
            sqlParser.setErrorStream( new FileOutputStream( "/Users/chris/shop-sql-parser.err" ) );
            
            dsp.addDataProcessor( sqlParser );

            DataStreamWriter writer = new DataStreamWriter( out, "\t" );
            writer.setKeys( "TIMESTAMP,@tree:mysql:query" );
            
            int limit = Integer.MAX_VALUE;
            Data item = dsp.readNext();
            while( item != null && limit-- > 0 ){
                System.out.println( "item: " + item );
                //System.out.println( "Writing output: " + out );
                if( item.containsKey( "@tree:mysql:query" ) )
                    writer.dataArrived( item );
                item = dsp.readNext();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit( -1 );
        }
    }
}