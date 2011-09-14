package org.jwall.sql.audit;

import java.text.SimpleDateFormat;
import java.util.Date;

import stream.data.Data;
import stream.data.DataProcessor;

public class DateParser
    implements DataProcessor
{
    String key;
    SimpleDateFormat fmt;
    
    public DateParser( String key, String fmt ){
        this.key = key;
        this.fmt = new SimpleDateFormat( fmt );
    }

    @Override
    public Data process(Data data)
    {
        if( data.containsKey( key ) ){
            String dat = data.get( key ).toString();
            try {
                Date date = fmt.parse( dat );
                data.put( "TIMESTAMP", date.getTime() + "" );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return data;
    }
}