package stream.filter;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;


/**
 * <p>
 * This filter simply checks the value of a given key/feature against a regular
 * expression. If the feature is <code>null</code> or the does <b>not</b> match
 * the regular expression, the data item is discarded.
 * </p>
 * 
 * @author chris@jwall.org
 *
 */
public class Include
    implements DataProcessor
{
    String key;
    String regex;
    
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
     * @return the regex
     */
    public String getRegex()
    {
        return regex;
    }

    /**
     * @param regex the regex to set
     */
    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    /**
     * @see stream.data.DataProcessor#process(stream.data.Data)
     */
    @Override
    public Data process(Data data){
        
        if( regex == null || key == null )
            return data;
        
        Serializable val = data.get( key );
        if( val != null && val.toString().matches( regex ) )
            return data;
        
        return null;
    }
}