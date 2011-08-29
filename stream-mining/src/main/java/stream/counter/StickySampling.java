package stream.counter;

import stream.learner.Learner;
import stream.model.Model;

/**
 * {@link Learner}-part of the implementation of the "Sticky Sampling" algorithm as described in the paper
 * "Approximate Frequency Counts over Data Streams" written by Gurmeet Singh Manku
 * and Rajeev Motwani
 *
 * @author Benedikt Kulmann
 * @see StickySamplingModel
 */
public final class StickySampling<T> implements Learner<T, CountModel<T>> {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>Elements which are not yet existing in the data structure will be added to it
	 * with probablity <code>1/r</code>.</p>
	 *
	 * <p><code>(samplingRate * t)</code> is the number of items
	 * until the {@link #adaptNewSamplingRate()} method will be invoked since its last invocation.</p>
	 */
	private long samplingRate;

	/**
	 * <p>Calculated at object creation time<br />
	 * have a look at the {@link #samplingRate} for further documentation</p>
	 */
	private final double t;

	/**
	 * <p>Counter for the current sampling interval so that it is possible to determine whether
	 * the {@link #adaptNewSamplingRate()} method has to be invoked.</p>
	 */
	private long windowCount;

	/**
	 * <p>The length of the current "sampling window", determined with <code>(samplingRate * t)</code></p>
	 */
	private long windowLength;

	/**
	 * <p>{@link Model} which holds a data structure for item counting and provides methods for result extraction.</p>
	 */
	private final StickySamplingModel<T> model;
	
	
	/**
	 * <p>Creates a new instance of StickySampling.</p>
	 *
	 * @param support The threshold whether an element is frequent or not. Has to be out of (0,1).
	 * @param error An epsilon for the threshold. Has to be out of (0,1).
	 * @param probabilityOfFailure Probability for an item to fail to fulfill the three quality characteristics of this algorithm. Has to be out of (0,1).
	 */
	public StickySampling(double support, double error, double probabilityOfFailure) {
		if(probabilityOfFailure <= 0 || probabilityOfFailure >= 1) {
			throw new IllegalArgumentException("Probability of failure has to be > 0 and < 1.");
		}

		this.samplingRate = 1;
		this.t = (1 / error) * Math.log(1 / (support * probabilityOfFailure));
		this.windowCount = 0;
		this.windowLength = (long)(2 * t);//only on initialization. Later this is calculated by (samplingRate * t)
		this.model = new StickySamplingModel<T>(support, error);
	}
	
	
	public void init(){
		
	}
	

	/**
	 * {@inheritDoc}
	 *
	 * <p>Checks whether the provided item already exists within the data structure. If true,
	 * the associated counter will be incremented by 1. If false, the item might be added
	 * to the data structure - the decision is performed by {@link #sample()}.</p>
	 *
	 * <p>Also checks whether the sampling rate needs to be changed and performs all
	 * necessary modifications if this is the case.</p>
	 */
	@Override
	public void learn(T item) {
		if(model.containsItem(item)) {
			model.incrementCount(item);
		} else {
			if(sample()) {
				model.insertEntry(item);
			}
		}
		windowCount++;

		if(changeOfSamplingRateNeeded()) {
			changeSamplingRate();
			adaptNewSamplingRate();
		}
	}

	/**
	 * <p>Decision whether a new item should be put into the data structure.</p>
	 *
	 * @return Whether an item should be put into the data structure
	 */
	private boolean sample() {
		return Math.random() <= 1 / (double)samplingRate;
	}

	/**
	 * <p>If the end of the current "sampling window" is reached, reset the current sampling window counter
	 * and increment the sampling rate.</p>
	 *
	 * @return Whether a change of the current sampling rate is needed
	 */
	private boolean changeOfSamplingRateNeeded() {
		return windowCount == windowLength;
	}

	/**
	 * <p>Makes changes to the sampling rate.</p>
	 *
	 * <p>Changes to the data structure will be performed by {@link #adaptNewSamplingRate()}</p>
	 */
	private void changeSamplingRate() {
		windowCount = 0;
		samplingRate *= 2;
		windowLength = (long)(samplingRate * t);
	}

	/**
	 * <p>Diminish counts and remove elements which reach a count of 0.</p>
	 *
	 * <p>This transforms the data structure such that it contains elements
	 * which would also have been sampled with the new sampling rate, only.</p>
	 *
	 * <p>The modification of the sampling rate itself is performed by {@link #changeSamplingRate()}.</p>
	 */
	private void adaptNewSamplingRate() {
		for(T item : model.keySet()) {
			while(tossCoin()) {
				model.decrementCount(item);
				if(model.frequencyIsZero(item)) {
					model.removeItem(item);
					break;
				}
			}
		}
	}

	/**
	 * <p>50:50 random event</p>
	 * 
	 * @return Result of 50:50 random event
	 */
	private boolean tossCoin() {
		return Math.random() < 0.5;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CountModel<T> getModel() {
		return model;
	}
}