package stream.data.mapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import stream.data.Data;
import stream.data.DataProcessor;

public class MapValues
    implements DataProcessor
{

    String key = "@label";
    
    Serializable defaultValue = new Double( -1.0d );
    Map<Serializable,Serializable> mapping = new HashMap<Serializable,Serializable>();
    

    public void addMapping( Serializable from, Serializable to ){
        mapping.put( from, to );
    }

    
    public void setDefault( Serializable defaultValue ){
        this.defaultValue = defaultValue;
    }

    public Serializable getDefault(){
        return this.defaultValue;
    }
    
    
    @Override
    public Data process(Data data)
    {
        Serializable val = data.get( key );
        if( val == null )
            return data;

        Serializable to = mapping.get( val );
        if( to == null ){
            data.put( key, to );
        } else {
            data.put( key, defaultValue );
        }
        
        return data;
    }
}