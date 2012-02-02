package stream.data.mapper;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;

public class NGrams
    implements DataProcessor
{
    static Logger log = LoggerFactory.getLogger( NGrams.class );
    String key = null;
    Integer n = 3;
    
    @Override
    public Data process(Data data) {
        
        if( key != null && n != null && n >= 0 ){
            
            Map<String,Double> counts = new LinkedHashMap<String,Double>();
            
            Serializable val = data.get( key );
            if( val != null ){

                String str = val.toString();
                for( int i = 0; i < str.length() - n; i++ ){
                    String ngram = str.substring( i, i + n );
                    
                    Double freq = counts.get( ngram );
                    if( freq != null ){
                        freq = freq + 1.0d;
                    } else {
                        freq = 1.0d;
                    }
                    counts.put( ngram, freq );
                }
                
                for( String key : counts.keySet() ){
                    data.put( key, counts.get( key ) );
                }

                log.debug( "Added {} {}-grams to item", counts.size(), n );
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
     * @return the n
     */
    public Integer getN()
    {
        return n;
    }


    /**
     * @param n the n to set
     */
    public void setN(Integer n)
    {
        this.n = n;
    }
}