package org.jwall.sql.audit;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;

public class FixSQLMapLabels
    implements DataProcessor
{
    static Logger log = LoggerFactory.getLogger( FixSQLMapLabels.class );
    String key;
    String labelKey;
    
    
    @Override
    public Data process(Data data)
    {
        
        if( key != null && labelKey != null ){
            Serializable val = data.get( key );
            if( val != null ){
                if( val.toString().matches( "^SELECT id,name,description,price FROM products WHERE id = \\d+$" ) ){
                    log.debug( "Labelling '{}' as 'normal'", val );
                    data.put( labelKey, "normal" );
                }
            }
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


    /**
     * @return the labelKey
     */
    public String getLabelKey()
    {
        return labelKey;
    }


    /**
     * @param labelKey the labelKey to set
     */
    public void setLabelKey(String labelKey)
    {
        this.labelKey = labelKey;
    }
}