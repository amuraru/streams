package stream.counter;

import java.util.ArrayList;
import java.util.Collection;

import stream.learner.Learner;


/**
 * <p>
 * {@link Learner}-part of the implementation of the Lossy Counting algorithm described in the paper
 * "Approximate Frequency Counts over Data Streams" written by 'Rajeev Motwani' and
 * 'Gurmeet Singh Manku'.
 * </p>
 * 
 * @author Marcin Skirzynski (main work), Benedikt Kulmann (modifications)
 * @see LossyCountingModel
 *
 * @param <T>
 */
public class LossyCounting<T> implements Learner<T, CountModel<T>> {

    private static final long serialVersionUID = 1L;

    /**
     * Model which holds a data structure for item counting and provides methods for result extraction.
     */
    private LossyCountingModel<T> model;

    /**
     * The window size which will be set at
     * the beginning and will never change
     */
    private int windowSize;

    /**
     * The number of the current window
     * beginning with 0
     */
    private long currentWindow;

    Double error;
    
    /**
     * <p>
     * Constructs an instance of the LossyCounting algorithm
     * with the specified maximum error bound, which can not
     * be changed.
     * </p>
     *
     * @param maxError the maximum error bound
     */
    public LossyCounting(double maxError) {
        if (maxError < 0 || maxError > 1) {
            throw new IllegalArgumentException("Maximal error needs to be a double between 0 and 1");
        }

        this.windowSize = (int) Math.ceil(1 / maxError);
        this.currentWindow = 1;
        this.model = new LossyCountingModel<T>(maxError);

        updateCurrentWindow();
    }

    
    
    /**
	 * @return the error
	 */
	public Double getEpsilon() {
		return error;
	}



	/**
	 * @param error the error to set
	 */
	public void setEpsilon(Double error) {
		this.error = error;
	}



	public void init(){
        if (error < 0 || error > 1) {
            throw new IllegalArgumentException("Maximal error needs to be a double between 0 and 1");
        }

        this.windowSize = (int) Math.ceil(1 / error.doubleValue() );
        this.currentWindow = 1;
        this.model = new LossyCountingModel<T>( error );

        updateCurrentWindow();
    }

    
    
    /**
     * <p>
     * Compresses the data structure. Will be called automatically
     * by the count method, when a new window is reached.
     * </p>
     */
    private void compress() {
        Collection<T> markedToRemove = new ArrayList<T>();
        for (T element : model.getDataStructure().keySet()) {
            CountEntryWithMaxError<T> entry = model.getDataStructure().get(element);
            if (entry.frequency + entry.maxError < currentWindow) {
                markedToRemove.add(element);
            }
        }
        for (T element : markedToRemove) {
            model.getDataStructure().remove(element);
        }
    }

    /**
     * <p>
     * Updates the current window
     * </p>
     */
    private void updateCurrentWindow() {
        this.currentWindow = (int) Math.ceil(model.getTotalCount() / (double) windowSize);
    }

    /**
     * <p>
     * Increments the item count of the provided item.
     * Afterwards executes a compress method.
     * </p>
     * 
     * @param item The item to count
     */
    @Override
    public void learn(T item) {
        if (model.containsItem(item)) {
            model.incrementCount(item);
        } else {
            model.insertNewItem(item, 1, currentWindow - 1);
        }

        updateCurrentWindow();
        if (model.getTotalCount() % windowSize == 0) {
            compress();
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
