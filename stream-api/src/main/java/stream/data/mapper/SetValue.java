package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.Description;

@Description( group="Data Stream.Processing.Transformations.Data" )
public class SetValue
    implements DataProcessor
{
    String key;
    String value;
    
    
    /**
     * 
     */
    @Override
    public Data process(Data data) {
        if( key != null && value != null ){
            data.put( key, value );
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
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}