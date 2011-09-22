package org.jwall.sql.audit;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataImpl;
import stream.io.CsvStream;
import stream.io.DataStreamWriter;

public class CrossJoin
{
    static Logger log = LoggerFactory.getLogger( CrossJoin.class );

    public static List<Data> read( URL url ) throws Exception {
        List<Data> list = new ArrayList<Data>();
        try {
            CsvStream stream = new CsvStream( url, "\t" );
            Data item = stream.readNext();
            while( item != null ){
                list.add( item );
                item = stream.readNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        URL s1 = new URL( "file:/Users/chris/test-php.tsv" );
        List<Data> urlData = read( s1 );

        URL s2 = new URL( "file:/Users/chris/test-queries.tsv" );
        List<Data> sqlData = read( s2 );
        Iterator<Data> it = sqlData.iterator();
        while( it.hasNext() ){
            Data sql = it.next();
            log.info( "item: {}", sql );
            Long ts = new Long( sql.get( "TIMESTAMP" ) + "" );
            if( ts < 1315815581000L ){
                it.remove();
                log.info( "Removing query {}", sql );
            }
        }
        System.out.println( "need to create " + (sqlData.size() * urlData.size()) + " pairs" );

        DataStreamWriter writer = new DataStreamWriter( new FileOutputStream( "/Users/chris/timestamped-sql-request.tsv" ), "\t" );
        int count = 0;
        int limit = 50000;
        Integer match = 0;
        Integer mismatch = 0;

        for( Data url : urlData ){
            log.info( "url: {}", url );
            Long time = new Long( url.get( "TIMESTAMP" ) + "" );
            for( Data sql : sqlData ){
                Long urlTime = new Long( sql.get( "TIMESTAMP" ) + "" );
                Long delta = time - urlTime;
                boolean store = false;

                if( Math.abs( delta ) < 2000 ){
                    match++;
                    store = true;
                }

                /*
                 */
                if( Math.abs( delta ) >= 2000 && Math.abs( delta ) < 10000 ){
                    store = true;
                    mismatch++;
                }

                if( store ){
                    Data out = new DataImpl();
                    out.put( "timestamp:sql", time.toString() );
                    out.put( "timestamp:request", urlTime.toString() );
                    out.put( "timestamp:delta", delta.toString() );
                    out.putAll( url );
                    out.putAll( sql );

                    writer.dataArrived( out );
                }
                if( ++count % 100 == 0 )
                    System.out.println( count + " items created." );

                if( count > limit ){
                    break;
                }
            }

            if( count > limit ){
                break;
            }
        }


        System.out.println( "Found " + match + " matches" );
        System.out.println( "Selected " + mismatch + " mis-matches" );
    }
}