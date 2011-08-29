
package stream.quantiles;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

import stream.model.SelectiveDescriptionModel;
import stream.model.SlidingWindow;

/**
 * This deterministic quantile estimator loosely adapts the idea described in chapter 5 of <i>
 * "Approximate Counts and Quantiles over Sliding Windows"</i> by <i>Arvind Arasu</i> and <i>Gurmeet
 * Singh Manku</i>. In detail the data structure described there is used in this class. The query 
 * algorithm used here differs from the paper. In a nutshell the data structure works as follows:<br>
 * The fixed size window (please refer to {@link #setWindowSize(int)} to change the default value)
 * of a stream gets copied L times, which generated L <it>levels</it>. At one level the stream gets 
 * partitioned into equal sized blocks. Each level has a block size of it's own. <br>
 * At each block runs an instance of {@link GKQuantiles}. To query a quantile, the blocks are used to 
 * reassemble the window, choosing disjoint blocks of different level, starting with big blocks and 
 * filling the remaining spaces using smaller sized blocks.<br>  
 * 
 * @author Markus Kokott
 */
public class WindowSketchQuantiles extends DeterministicQuantileEstimator {
	private static final long serialVersionUID = 8629450116663341157L;
	
	private Long elementCount;
	private int windowSize;
	private int maxLevel;
	
	private GKQuantiles initialGK;
	
	private LinkedList<Block> levels = new LinkedList<Block>();
	private LinkedList<SlidingWindow<Quantiles>> quantiles;
	
	/**
	 * Because this class extends {@link QuantileEstimator} this constructor
	 * needs a parameter determining the error bound.
	 * @param epsilon <code>float</code> that represents the error bound.
	 */
	public WindowSketchQuantiles(float epsilon) {
		super(epsilon);
		
		initialGK = new GKQuantiles(epsilon);
		
		this.quantiles = new LinkedList<SlidingWindow<Quantiles>>();
		this.elementCount = 0L;
		this.setWindowSize(32768);
		
		Float value = 1 / epsilon;
		super.epsilon = 1 / this.floorToNextPowerOfTwo(value);
		
		this.prepareLevels();
	}
	
	
	public SelectiveDescriptionModel<Double, Double> getModel() {
		return new SelectiveDescriptionModel<Double, Double>() {
			private static final long serialVersionUID = -1507342947881040783L;

			@Override
			public Double describe(Double phi) {
				return getQuantile(phi);
			}
		};
	}

	
	//---------------------------------------------------------------------------//
	//-                 ----- interface implementation methods ----              // 
	//---------------------------------------------------------------------------//
	
	public Double getQuantile(double phi) {
		
		if (this.elementCount < this.windowSize){
			return this.initialGK.getQuantile(phi);
		}
		
		//LinkedList<ComparableWrapper<Double, Float>> sortedSummary = this.getOrderedSummary();
		
		LinkedList<Double> summary = this.getFinalSummary();
		
		Double rank = phi * summary.size();
		Double quantile = summary.get(rank.intValue());
		
		/*
		Double border = this.getBorderElement(phi, (LinkedList<ComparableWrapper<Double,Float>>) sortedSummary.clone());
		Double quantile = this.getPhiElement(phi, border, sortedSummary);
		*/
		
		return quantile;
	}

	/**
	 * @see edu.udo.cs.pg542.util.DataStreamProcessor#process(java.lang.Object)
	 */
	@Override
	public void learn(Double item) {
		this.incrementCount();
		this.insertElement(item);
		
		if (this.elementCount < this.windowSize){
			this.initialGK.learn(item);
		}
		
		this.slideWindow();
	}
	
	//---------------------------------------------------------------------------//
	//-                       ----- PRELIMINARY methods ----                     // 
	//---------------------------------------------------------------------------//
	/**
	 * Creates one block per level. This block is the block with state <b>UNDER-CONSTRUCTION</b> that will
	 * be filled with new arriving elements. The blocks are stored in increasing order.
	 */
	private void prepareLevels() {
		this.computeMaximumLevel();
		
		Double blockSize = this.computeMinBlockSize();
		Float levelEpsilon = this.computeEpsilonForMinLevel();
		
		for (int i = 0; i < this.maxLevel + 1; i++){
			Block newBlock = new Block (levelEpsilon, blockSize.intValue());
			levels.addLast(newBlock);
			blockSize *= 2;
			levelEpsilon /= 2;
			
			SlidingWindow<Quantiles> newWindow = new SlidingWindow<Quantiles>(this.windowSize);
			this.quantiles.add(newWindow);
		}
	}
	
	/**
	 * Computes the <code>integer</code> value of the maximum level (which depends on epsilon)
	 * and puts it into <code>maxLevel</code>.
	 */
	private void computeMaximumLevel() {
		Double maxLevel = Math.log10(4 / super.epsilon) / Math.log10(2); 
		this.maxLevel = maxLevel.intValue();
	}
	
	/**
	 * Computes the error bound of the smallest level.
	 * @return {@link Float} value determining the error of level zero.
	 */
	private Float computeEpsilonForMinLevel(){
		int divisor = 2 * (2* this.maxLevel + 2);
		Double epsilon = (this.epsilon * Math.pow(2, this.maxLevel)) / divisor;
		
		return epsilon.floatValue();
	}
	
	/**
	 * Computes the size of blocks at level zero.
	 * @return the smallest block size represented by a {@link Double} value.
	 */
	private Double computeMinBlockSize(){
		
		Double minBlockSize = this.epsilon * this.windowSize;
		return (minBlockSize.doubleValue() / 4);
	}
	
	/**
	 * By default the window size is set to 32768. You can reset this size any time, but
	 * please note, that this will delete the current summary. So you'll probably wish to
	 * set the window size once after you have initiated a new instance of this class.
	 * 
	 * @param windowSize <code>int</code> value that will be ceiled to the next power of two
	 * before reseting the window size to that value.
	 */
	public final void setWindowSize(int windowSize){
		windowSize = this.ceilToNextPowerOfTwo(windowSize);
		
		// smaller windows doesn't make any sense
		if (windowSize <= 128){
			return;
		}
		
		this.quantiles = new LinkedList<SlidingWindow<Quantiles>>();
		this.elementCount = 0L;
		this.windowSize = windowSize;
		
		this.prepareLevels();
	}
	
	
	//---------------------------------------------------------------------------//
	//-                          ----- INSERT methods ----                       // 
	//---------------------------------------------------------------------------//
	/**
	 * Inserts a given item into the data structure. While a {@link Block} hasn't reached its maximum size,
	 * the item will just be transfered to an instance of {@link GKQuantiles} managed by this {@link Block}. 
	 * When the {@link Block} becomes full a summary is created and put in a {@link SlidingWindow}.
	 */
	private void insertElement(Double item){
		
		for (int i = 0; i < this.maxLevel + 1; i++){
			this.levels.get(i).insert(item);
			
			if (this.elementCount % this.levels.get(i).getBlockSize() == 0){
				
				Quantiles newQuantiles = new Quantiles (this.levels.get(i).getEpsilon(), this.levels.get(i).getSummary());
				this.quantiles.get(i).add(newQuantiles, this.levels.get(i).getBlockSize());
			}
		}
	}
	
	/**
	 * Increments the element count by one
	 */
	private void incrementCount(){
		this.elementCount++;
	}
	
	
	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append( getClass().getCanonicalName() );
		s.append( " {" );
		s.append( " epsilon=" + this.getEpsilon() );
		s.append( " }" );
		return s.toString();
	}

	//---------------------------------------------------------------------------//
	//-                       ----- ESTIMATION  methods ----                     // 
	//---------------------------------------------------------------------------//
	/**
	 * This method checks all blocks of each level and creates an ensemble that minimizes
	 * the epsilon of this ensemble. This is done by choosing disjoint blocks with maximal size,
	 * i.e. if the block of <code>maxLevel</code> is active, we found the most accurate summary.
	 * If this block is in the state <b>UNDER-CONSTRUCTION</b> right now, it will take the <b>
	 * ACTIVE</b> block at level <code>maxLevel - 1</code> and fills the missing partitions by
	 * choosing appropriate blocks at lower levels.
	 * 
	 * @return {@link LinkedList} of {@link Quantiles} that contains the summary of the stream that
	 * have the smallest epsilon compared to all other possible summaries.
	 */
	private LinkedList<Quantiles> getStreamSummary(){
		LinkedList<Quantiles> summary = new LinkedList<Quantiles>();
		
		// if the highest level contains an ACTIVE element, this element will cover the whole window
		if (!this.quantiles.get(maxLevel).isEmpty()){
			summary.add(this.quantiles.get(maxLevel).getNewestElement());
			return summary;
		}
		
		// if there is no ACTIVE element in the highest level we add the only active element of the next
		// lower level into the summary. There is at most one element active, because if there would be
		// two elements, the block in the highest level must be ACTIVE, too.
		Quantiles bigBlock = this.quantiles.get(maxLevel - 1).getNewestElement();
		
		// interval [ 0 ; leftBorder ] not covered yet
		int leftBorder = this.quantiles.get(maxLevel - 1).getLifeTime(0);
		// interval [ rightBorder ; windowSize ] not covered yet
		int rightBorder = this.quantiles.get(maxLevel - 1).getLifeTime(0) + this.quantiles.get(maxLevel - 1).getSize(0);
		// there are at most two intervals uncovered. i.e. at the beginning and at the end of the window.
		int maxUncovered = this.quantiles.get(0).getSize(0);
		
		// next level
		int level = this.maxLevel - 2;
		// covering the left uncovered interval
		while (level >= 0 && leftBorder > maxUncovered){
			
			for (int i = this.quantiles.get(level).getAll().size() - 1; i > -1; i--){
				
				if (leftBorder > this.quantiles.get(level).getLifeTime(i)){
					
					leftBorder = this.quantiles.get(level).getLifeTime(i);
					summary.addFirst(this.quantiles.get(level).get(i));
				}
			}
			
			level--;
		}
		
		summary.add(bigBlock);
		
		// next level
		level = this.maxLevel - 2;
		// covering the right uncovered interval
		while (level >=0 && rightBorder < this.windowSize - maxUncovered){
			
			for (int i = 0; i < this.quantiles.get(level).getAll().size(); i++){

				if (rightBorder < this.quantiles.get(level).getLifeTime(i) + this.quantiles.get(level).getSize(i)){
					
					rightBorder = this.quantiles.get(level).getLifeTime(i) + this.quantiles.get(level).getSize(i);
					summary.addLast(this.quantiles.get(level).get(i));
				}
			}
			
			level--;
		}
		
		return summary;
	}
	
	/**
	 * Because elements of bigger sized blocks are more important than elements of smaller sized
	 * blocks, this method returns a "weighted" summary. I.e., elements of bigger sized blocks
	 * will appear more often in the summary than elements of smaller sized blocks.
	 * @return {@link LinkedList} of {@link Double} representing the window summary. 
	 */
	private LinkedList<Double> getFinalSummary(){
		
		LinkedList<Quantiles> summary = this.getStreamSummary();
		LinkedList<Double> finalSummary = new LinkedList<Double>();
		
		// for each block
		for (int i = 0; i < summary.size(); i++){
			
			// for each quantile in the current block
			Float weight = this.computeLevelForEpsilon(summary.get(i).getEpsilon());
			
			for (int j = 0; j < summary.get(i).getQuantiles().size(); j++){
				
				for (int k = 0; k <= weight; k++){
					finalSummary.addAll(summary.get(i).getQuantiles());
				}
			}
		}
		
		Collections.sort(finalSummary);
		return finalSummary;	
	}
	
	/**
	 * Given a {@link Block}'s value of epsilon this method computes its level.
	 * @param epsilon - an error parameter
	 * @return the level at which {@link Block}s with epsilon are found. 
	 */
	private Float computeLevelForEpsilon(Float epsilon) {
		Double argument = 2 * epsilon * ( 2 * this.maxLevel +2) / this.epsilon; 
		Double level = this.maxLevel - Math.log(argument) / Math.log(2);
		
		return level.floatValue();
	}
	
	//---------------------------------------------------------------------------//
	//-                      ----- MISCELLANEOUS methods ----                    // 
	//---------------------------------------------------------------------------//
	/**
	 * Rounds the given value to the next power of two that is greater than the value.
	 * @param value
	 * @return
	 */
	private int ceilToNextPowerOfTwo(int value){
		Double powerOfTwo = Math.pow(2, Math.floor( Math.log10(value) / Math.log10(2) ));
		return powerOfTwo.intValue();
	}
	
	/**
	 * Rounds the given value to the next power of two that is smaller than the value.
	 * @param value
	 * @return
	 */
	private Float floorToNextPowerOfTwo(Float value){
		Double powerOfTwo = Math.pow(2, Math.ceil( Math.log10(value) / Math.log10(2) ));
		return powerOfTwo.floatValue();
	}
	
	/**
	 * moves the elements in the {@link SlidingWindow} one position to the end of the window.
	 */
	private void slideWindow(){
		
		for (int i = 0; i < this.maxLevel + 1; i++){
			this.quantiles.get(i).slideWindowByOnePosition();
		}
	}
	
	/**
	 * 
	 * This inner class holds the {@link GKQuantiles} for a specific level.
	 * {@link Block}s of different levels vary in their block size and error bound epsilon.
	 *
	 */
	//---------------------------------------------------------------------------//
	//-                         ----- INNER CLASS Block  ----                    // 
	//---------------------------------------------------------------------------//
	public class Block implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 7802824333706107860L;
		private Float epsilon;
		private Integer blockSize;
		
		private LinkedList<Double> summaryOfLastBlock;
		private GKQuantiles quantileEstimator;
		
		public Block(Float epsilon, Integer blockSize){
			this.epsilon = epsilon;
			this.blockSize = blockSize;
			this.quantileEstimator = new GKQuantiles(epsilon);
			this.summaryOfLastBlock = new LinkedList<Double>();
		}
		
		public void insert(Double item){
			this.quantileEstimator.learn(item);
			
			if (this.quantileEstimator.getCount() == blockSize){
				this.createSummary();
				quantileEstimator = new GKQuantiles(epsilon);
			}
		}
		
		public Integer getBlockSize(){
			return this.blockSize;
		}
		
		public LinkedList<Double> getSummary(){
			return this.summaryOfLastBlock;
		}
		
		public Float getEpsilon(){
			return this.epsilon;
		}
		
		private void createSummary(){
			Float phi = this.epsilon;
			LinkedList<Double> summary = new LinkedList<Double>();
			
			while (phi <= 1){
				summary.add(quantileEstimator.getQuantile(phi));
				phi += this.epsilon;
			}
			
			this.summaryOfLastBlock = summary;
		}
	}
	
	/**
	 * 
	 * Just a wrapper class for a summary (i.e. {@link LinkedList} of {@link Double} representing 
	 * quantiles and a {@link Float} value representing its error bound epsilon.
	 *
	 */
	//---------------------------------------------------------------------------//
	//-                     ----- INNER CLASS Quantiles  ----                    // 
	//---------------------------------------------------------------------------//
	public class Quantiles implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -6060440214958903531L;
		private Float epsilon;
		private LinkedList<Double> quantiles = new LinkedList<Double>();
		
		public Quantiles(Float epsilon, LinkedList<Double> quantiles){
			this.epsilon = epsilon;
			this.quantiles = quantiles;
		}
		
		public Double getQuantile(float phi){
			int position = Math.round(phi * this.quantiles.size());
			
			return this.quantiles.get(position);
		}
		
		public LinkedList<Double> getQuantiles(){
			return this.quantiles;
		}
		
		public Float getEpsilon(){
			return this.epsilon;
		}
	}
}
