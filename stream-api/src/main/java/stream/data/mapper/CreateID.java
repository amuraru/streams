package stream.data.mapper;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.util.Description;
import stream.util.Parameter;

/**
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Description( 
		text="This processor tags all processed items with integer IDs.",
		group="Data Stream.Processing.Annotations" 
)
public class CreateID
    implements DataProcessor
{
	Long start = 0L;
    Long nextId = 0L;
    String key = "@id";

    /**
     * @see stream.data.DataProcessor#process(stream.data.Data)
     */
    @Override
    public Data process(Data data){
    	
    	
        if( key != null ){
        	synchronized( nextId ){
        		data.put( key, nextId++ );
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
    @Parameter( defaultValue="@id" )
    public void setKey(String key)
    {
        this.key = key;
    }
    
    
    @Parameter( defaultValue="0" )
    public void setStart( Long l ){
    	start = l;
    	nextId = start;
    }
    
    public Long getStart(){
    	return start;
    }
}