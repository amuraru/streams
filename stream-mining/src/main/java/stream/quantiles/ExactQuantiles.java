package stream.quantiles;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import stream.model.SelectiveDescriptionModel;


/**
 * This is a simple implementation to determine exact quantiles. It's just for
 * testing/validating purpose so it won't be the most efficient algorithm neither 
 * in respect to space consumption nor in respect to time consumption. And of 
 * course it is not an online algorithm!
 * 
 * @author Markus Kokott
 *
 */
public class ExactQuantiles implements QuantileLearner{
	private static final long serialVersionUID = -5288629869227691726L;

	private Map<Double,Long> elements;
	private long overallCount;
	TreeSet<Double> data = new TreeSet<Double>();

	/**
	 * Creates a new instance of {@link ExactQuantiles}
	 */
	public ExactQuantiles(){
		init();
	}

	
	public void init(){
		this.elements = new ConcurrentHashMap<Double, Long>();
		this.overallCount = 0L;
	}
	
	
	@Override
	public SelectiveDescriptionModel<Double, Double> getModel() {
		return new SelectiveDescriptionModel<Double, Double>() {
			private static final long serialVersionUID = -6028955231084094144L;

			@Override
			public Double describe(Double phi) {
				return getQuantile(phi);
			}
		};
	}

	public Double getQuantile(double phi) {
		final long overallCountCopy = new Long(overallCount);
		final long rank = (long)Math.floor(phi * overallCountCopy);

		long countSum = 0L;
		for(Double val : data ) {
			Long count = elements.get( val );
			countSum += count;
			if(rank <= countSum) {
				return val;
			}
		}
		return Double.NaN;
	}

	public void writeData( OutputStream out ){
		PrintStream p = new PrintStream( out );

		//long countSum = 0L;
		for( Double entry : data ) {
			Long count = elements.get( entry );
			//countSum += count;
			p.println( entry + " " + count );
		}
		p.flush();
		p.close();
	}


	/**
	 * @see edu.udo.cs.pg542.util.DataStreamProcessor#process(java.lang.Object)
	 */
	@Override
	public void learn(Double item) {
		Long count = elements.get( item );
		if( count == null ){
			elements.put( item, new Long(1L) );
			data.add( item );
		} else
			elements.put( item, count + 1 );
		
		this.overallCount++;
	}
	
	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append( getClass().getCanonicalName() );
		s.append( " {" );
		s.append( " }" );
		return s.toString();
	}

}