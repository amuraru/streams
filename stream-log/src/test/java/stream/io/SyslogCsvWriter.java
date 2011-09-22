package stream.io;

import java.io.FileOutputStream;
import java.net.URL;

import stream.data.Data;

public class SyslogCsvWriter
{

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        URL url = new URL( "file:/Users/chris/kirmes-shop-database.log" );
        DataStream stream = new SyslogDataStream( url );
        DataStreamWriter out = new DataStreamWriter( new FileOutputStream( "/Users/chris/syslog-out.csv" ), ";" );
        out.setKeys( "TIMESTAMP,MESSAGE" );
        Data item = stream.readNext();
        while( item != null ){
            //item.remove( "DATA" );
            out.dataArrived( item );
            item = stream.readNext();
        }
        
        out.close();
    }

}
