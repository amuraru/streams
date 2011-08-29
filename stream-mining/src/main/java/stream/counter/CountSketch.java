package stream.counter;

import stream.counter.hashing.SimpleHashFactory;
import stream.learner.Learner;


/**
 * <p>
 * {@link Learner}-part of the implementation of the CountSketch algorithm from the paper
 * 'Finding frequent items in data streams' written by 
 * 'Charikar, M., Chen, K., and Farach-colton, M. (2002)'.
 * </p>
 * 
 * @author Marcin Skirzynski (main work), Benedikt Kulmann (modifications)
 * @see CountSketchModel
 *
 * @param <T>
 */
public class CountSketch<T> implements Learner<T, CountModel<T>> {

    private static final long serialVersionUID = 1L;

    protected CountSketchModel<T> model;

    /**
     * <p>
     * Constructor of the CountSketch algorithm. This construction
     * can take quite a long time since the construction of the hashfunctions
     * is rather time-consuming.
     * </p>
     *
     * @param domain The (estim.) domain, i.e. how many different items are expected
     * @param numberOfHashFunctions The number of hashfunctions which determine a bucket
     * @param numberOfBuckets The number of buckets where a counter will be maintained
     * @param k parameter for the top-k variant. If you want to disable
     * the top-k overhead, than set k to 0 or lower
     */
    public CountSketch(int domain, int numberOfHashFunctions, int numberOfBuckets, int k) {
        model = new CountSketchModel<T>(domain, numberOfHashFunctions, numberOfBuckets, k, new SimpleHashFactory<T>());
    }

    /**
     * Same as {@link #CountSketch(int, int, int, int)} but with disabled top-k.
     */
    public CountSketch(int domain, int numberOfHashFunctions, int numberOfBuckets) {
        this(domain, numberOfHashFunctions, numberOfBuckets, 0);
    }

    protected CountSketch() {
        // a class extending this class might want to use another model type...
    }
    
    public void init(){
    	
    }

    /**
     * <p>
     * Counts the item by passing it to the internal
     * data strucutre.
     * </p>
     *
     * <p>
     * If a k greater than zero was set, the top-k
     * map will be maintained also.
     * </p>
     *
     * @param item The item to count
     */
    @Override
    public void learn(T item) {

        boolean kGreaterZero = model.updateData(item);
        if (!kGreaterZero) {
            return;
        }

        if(model.isTopItem(item)) {
            model.incrementCount(item);
        } else if(model.notYetKItems()) {
            model.insertTopItem(item, 1L);
        } else {
            /**
             * Remove the item with the lowest frequency if the new item
             * has a higher frequency.
             */
             CountEntry<T> lowFreqItem = model.getItemWithLowestCount();
             long estimatedFreq = model.estimateFrequency(item);
             if(lowFreqItem.frequency < estimatedFreq) {
                 model.removeTopItem(lowFreqItem.item);
                 model.insertTopItem(item, estimatedFreq);
             }
        }
	System.out.println("number of elements: " + model.topItems.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountModel<T> getModel() {
        return model;
    }
}
