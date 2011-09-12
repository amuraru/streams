package stream.data.mapper;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.DataUtils;


/**
 * This class implements a simple strategy to map labels to { -1, +1 }
 * 
 * @author chris@jwall.org
 *
 */
public class BinaryLabels
    implements DataProcessor
{
    
    String key;
    String positive = null;

    @Override
    public Data process(Data data)
    {
        if( key == null ){
            for( String k : data.keySet() ){
                if( DataUtils.isAnnotation( k ) && k.startsWith( "@label" ) ){
                    key = k;
                    break;
                }
            }
        }
        
        if( key == null )
            return data;
        
        Serializable val = data.get( key );
        if( val == null )
            return data;
        
        if( val instanceof Double ){
            Double d =(Double)val;
            if( d < 0.0d ){
                data.put( key, -1.0d );
            } else 
                data.put( key, 1.0d );
            return data;
        }
        
        if( positive == null )
            positive = val.toString();
        
        if( positive.equals( val ) )
            data.put( key, 1.0d );
        else
            data.put( key, -1.0d );
        
        return data;
    }
}