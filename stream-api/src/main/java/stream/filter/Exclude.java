package stream.filter;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;


/**
 * <p>
 * This filter simply checks the value of a given key/feature against a regular
 * expression. If the feature value does match the regular expression, the data 
 * item is discarded.
 * </p>
 * <p>
 * A non-existing feature value (i.e. <code>null</code>) will be treated as the
 * string <code>null</code>.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class Exclude
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
        if( val == null )
            val = "null";
        
        if( val.toString().matches( regex ) )
            return data;
        
        return null;
    }
}