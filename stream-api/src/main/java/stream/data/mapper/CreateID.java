package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;

public class CreateID
    implements DataProcessor
{
    Long nextId = 0L;
    String key = "@id";

    /**
     * @see stream.data.DataProcessor#process(stream.data.Data)
     */
    @Override
    public Data process(Data data){
        if( key != null ){
            data.put( key, nextId++ );
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