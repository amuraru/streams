package stream.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import stream.data.Data;


/**
 * <p>
 * This class implements the SGD algorithm in a window based fashion. A sliding window of 
 * fixed size is maintained for the incoming examples and for each new example, the SGD
 * learn() method is applied multiple times over the current window.
 * </p>
 * <p>
 * The order of the window is shuffled before each pass.
 * </p>
 * <p>
 * For a windowSize of 1 and the number of passes set to 1, this behaves exactly like the
 * default SGD implementation.
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
public class WindowedSGD extends StochasticGradientDescent {

	/** The unique class ID */
	private static final long serialVersionUID = -1010895596597929892L;
	
	/* The number of passes for the current window */
	Integer passes = 1;
	
	/* The size of the window */
	Integer windowSize = 1;
	
	/* The window itself (data) */
	ArrayList<Data> window = new ArrayList<Data>();
	
	/* The order for sampling */
	ArrayList<Integer> order = new ArrayList<Integer>();

	Random rnd = new Random( 2011L );
	
	/**
	 * Creates a new instance of this algorithm with a default windowSize
	 * of 1000 examples.
	 */
	public WindowedSGD( SgdObjectiveFunction objFunction ){
		super( objFunction );
		setWindowSize( 1000 );
	}
	
	
	
	public Integer getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Integer windowSize) {
		this.windowSize = windowSize;
		window = new ArrayList<Data>( windowSize );
		order = new ArrayList<Integer>( windowSize );
		for( int i = 0; i < order.size(); i++ )
			order.add( new Integer( i ) );
		
		Collections.shuffle( order );
	}

	
	public Integer getPasses() {
		return passes;
	}

	public void setPasses(Integer passes) {
		this.passes = passes;
	}


	/**
	 * @see stream.optimization.StochasticGradientDescent#init()
	 */
	@Override
	public void init() {
		super.init();

		// ensure that the windowSize is at least 1
		//
		if( windowSize < 1 )
			windowSize = 1;
		
		// this will initialize the window
		//
		setWindowSize( windowSize );
	}



	/**
	 * @see stream.optimization.StochasticGradientDescent#learn(stream.data.Data)
	 */
	@Override
	public void learn(Data example) {
		
		if( window.size() > windowSize - 1 ){
			int idx = rnd.nextInt( window.size() );
			window.remove( idx );
		}
		window.add( example );
		
		// we only start with the window-passes if the window
		// has reached the specified window-size.
		//
		/*
		if( window.size() < windowSize ){
			super.learn( example );
			return;
		}
		 */
		
		Collections.shuffle( order );
		
		for( Integer idx : order ){
			super.learn( window.get( idx ) );
		}
	}
}