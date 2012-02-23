package stream.counter;

import java.io.Serializable;

import stream.learner.Learner;

/**
 * <p>
 * {@link Learner}-part of a naive counter implementation. Simply contains a counter
 * for each element which will be incremented in a deterministic way within the learn
 * method. Of course the purpose of this implementation is not a "live environment". Instead
 * it is intended to be used as "the truth" for evaluation intents.
 * </p>
 *
 * @author Benedikt Kulmann, office@kulmann.biz
 */
public class RealCounting<T extends Serializable> implements Learner<T, CountModel<T>> {

    private static final long serialVersionUID = 1L;

    /**
     * Model which contains all the count data for this counting implementation
     */
    private RealCountingModel<T> model;

    public RealCounting() {
        this(0.0);
    }

    public RealCounting(double minSupport) {
        this(minSupport, 0);
    }
    
    public RealCounting(int k) {
        this(0.0, k);
    }

    public RealCounting(double minSupport, int k) {
        model = new RealCountingModel<T>(minSupport, k);
    }
    
    
    public void init(){
    	
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void learn(T item) {
        if(model.containsItem(item)) {
            model.incrementCount(item);
        } else {
            model.insertItem(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountModel<T> getModel() {
        return model;
    }
}