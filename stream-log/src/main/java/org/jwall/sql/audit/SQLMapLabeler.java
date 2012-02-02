package org.jwall.sql.audit;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;

public class SQLMapLabeler
    implements DataProcessor
{
    String key;

    @Override
    public Data process(Data data)
    {
        if( key == null )
            return data;
        
        Serializable value = data.get( key );
        if( value != null && value.toString().indexOf( "sqlmap" ) >= 0 ){
            data.put( "@label", "attack" );
        } else {
            data.put( "@label", "normal" );
        }
        return data;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }
}