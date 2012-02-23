package stream.counter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.eval.size.SizeMeasurement;
import stream.learner.Learner;


/**
 * 
 * This is a simple implementation of a stream-counting model. The model is updatable and
 * will - for a given threshold <code>k</code> - approximate the counts of the top-k
 * elements within a example-set/stream. 
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class SimpleTopKCounting
    implements Comparator<String>, Learner<String,CountModel<String>>, CountModel<String>
{
    private static final long serialVersionUID = 4365995573179300743L;
    static Logger log = LoggerFactory.getLogger( SimpleTopKCounting.class.getName() );
    
    /** This map holds the list of monitored items and their counters */
    HashMap<String,Long> topK;
    
    
    /** This is the maximum number of observed items within the stream/example-set */
    int k;
    
    /** The number of elements that have been processed */
    Long cnt = 0L;
    
    
    /**
     * This initially creates a counting model for streams. The model will
     * not use more than the last <code>k</code> elements in order to
     * approximate the item counts within the stream.
     * 
     * @param examples The initial example set. Not that this will usually consist of a one-element example set.
     * @param k The maximum number of items that may be tracked/monitored by the model.
     */
    public SimpleTopKCounting( int k ){
        this.k = k;
        this.topK = new HashMap<String,Long>();
        log.debug( "Creating top-k counter with k = {}", k );
    }
    
    


	/**
	 * @see stream.learner.Learner#init()
	 */
	@Override
	public void init() {
		cnt = 0L;
		this.topK = new HashMap<String,Long>();
	}


    /**
     * This method actually does all the work when learning from the stream. It will update
     * the inner structures to reflect the incoporation of the given example.
     * 
     * @param ex
     */
    @Override
    public void learn( String example ){
        cnt++;
        if( cnt % 100 == 0 )
        	log.debug( "   space used: {}/{}", topK.size(), k );

        // is the element already in the list of our top-k monitored items? 
        //
        if( topK.get( example ) != null ){
            
            log.debug( "Incrementing count of top-k element {}", example );
            //LogService.getGlobal().logNote( "Current top-k list is:\n" + this.toResultString() );
            Long cnt = topK.get( example ) + 1;
            topK.put( example, cnt );
            
        } else {
            
            // we must not monitor more than k elements 
            //
            if( topK.size() >= k ){
                
            	log.debug("Need to replace the most in-frequent top-k element with {}", example );
                //LogService.getGlobal().logNote("Current top-k list is:\n" + this.toResultString() );
                //
                // find the one with the smallest count and replace it
                //
                Long min = 0L;
                String leastElement = null;
                
                for( String key : topK.keySet() ){
                    if( leastElement == null ){
                        min = topK.get( key );
                        leastElement = key;
                    } else {
                        if( topK.get( key ) < min ){
                            min = topK.get( key );
                            leastElement = key;
                        }
                    }                          
                }
                
                Long newCount = min + 1;
                topK.remove( leastElement );
                topK.put( example, newCount );
            } else {
                //
                // ok, there is space left in our monitor-list
                // 
                
                log.debug( "Enough space to add new element {}", example );
                log.debug( "   space used: {}/{}", topK.size(), k );
                if( topK.get( example ) != null )
                    log.warn( "Overwriting existing element with count {}", topK.get( example ) );
                    
                topK.put( example, 1L );
                
            }
            
        }
    }

    
    public Long getCount( String item ){
    	if( topK.containsKey( item ) )
    		return this.topK.get( item );
    	return 0L;
    }

    /**
     * This implements the Comparator interface which is used to sort the top-k elements
     * in order to find out the (k+1)-th element which is to be eliminated next.
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( String o1, String o2 ){
        
        if( o1 == o2 || o1.equals( o2 ) )
            return 0;
        
        Long i1 = topK.get( o1 );
        Long i2 = topK.get( o2 );

        int rc = i1.compareTo( i2 );
        if( rc == 0 )
        	return o1.compareTo( o2 );
        
        return (-1) * i1.compareTo( i2 );
    }


	/**
	 * @see stream.counter.CountModel#getTotalCount()
	 */
	@Override
	public Long getTotalCount() {
		return cnt;
	}


	/**
	 * @see stream.counter.CountModel#keySet()
	 */
	@Override
	public Set<String> keySet() {
		return topK.keySet();
	}


	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public Long predict(String item) {
		return getCount( item );
	}


	/**
	 * @see stream.learner.Learner#getModel()
	 */
	@Override
	public CountModel<String> getModel() {
		return this;
	}
	
	
	public void dumpSize(){
		log.info( "Simple TopK uses {} bytes", SizeMeasurement.sizeOf( this ) );
	}
}