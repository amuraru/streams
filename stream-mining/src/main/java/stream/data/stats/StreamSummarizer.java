/**
 * 
 */
package stream.data.stats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.counter.SimpleTopKCounting;
import stream.data.Data;
import stream.eval.HtmlResult;
import stream.io.DataStreamListener;
import stream.learner.LearnerUtils;

/**
 * <p>
 * This class implements a simple listener that creates statistics such as
 * maximum, average, etc. from incoming stream data.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class StreamSummarizer implements DataStreamListener, HtmlResult {

	/* The observed attribute types */
	Map<String,Class<?>> types = new LinkedHashMap<String,Class<?>>();
	
	/* Maxima of the nominal attributes */
	Statistics maxima = new Statistics();
	
	/* Minima of the numerical attributes */
	Statistics minima = new Statistics();
	
	/* Gather the approximated top 1000 elements of each nominal value */
	Map<String,SimpleTopKCounting> topk = new LinkedHashMap<String,SimpleTopKCounting>();
	
	Long elements = 0L;
	
	File file;

	public StreamSummarizer(){
	}
	
	public StreamSummarizer( File file ){
		this.file = file;
	}
	
	
	/**
	 * @see stream.io.DataStreamListener#dataArrived(stream.data.Data)
	 */
	@Override
	public void dataArrived(Data datum) {
		elements++;
		for( String key : datum.keySet() ){
			if( LearnerUtils.isNumerical( key, datum ) ){
				process( key, LearnerUtils.getDouble( key, datum ) );
			} else {
				process( key, datum.get( key ).toString() );
			}
		}
		
		if( elements % 100 == 0 ){
			if( file != null ){
				try {
					this.store( new FileOutputStream( file ) );
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//this.store( System.out );
			}
		}
	}
	
	
	protected void process( String key, Double value ){
		
		if( !types.containsKey( key ) )
			types.put( key, Double.class );
		
		// adjust the maximum for this key
		//
		Double m = maxima.get( key );
		if( m == null ){
			maxima.put( key, value );
		} else 
			maxima.put( key, Math.max( m, value ) );
		
		// adjust the minimum for this key
		//
		Double min = minima.get( key );
		if( min == null )
			minima.put( key, value );
		else
			minima.put( key, Math.min( min, value ) );
	}
	
	
	protected void process( String key, String value ){
		if( !types.containsKey( key ) )
			types.put( key, String.class );
		
		SimpleTopKCounting top = topk.get( key );
		if( top == null ){
			top = new SimpleTopKCounting( 1000 );
			topk.put( key, top );
		}
		top.learn( value );
	}
	
	
	
	public void store( OutputStream out ){
		PrintStream p = new PrintStream( out );
		
		p.println( "<table class=\"summary\">" );
		StringBuffer header = new StringBuffer( "<tr>" );
		StringBuffer typeRow = new StringBuffer( "<tr>" );
		StringBuffer statRow = new StringBuffer( "<tr>" );
		
		for( String key : types.keySet() ){
			header.append( "<th>" + key + "</th>" );
			typeRow.append( "<td>" + types.get( key ).getName() + "</td>" );
			
			if( types.get( key ) == Double.class ){
				statRow.append( "<td>" );
				statRow.append( "Minimum: " + minima.get(key ) + "<br/>" );
				statRow.append( "Maximum: " + maxima.get(key ) + "<br/>" );
				statRow.append( "</td>" );
			} else {
				statRow.append( "<td>" );
				SimpleTopKCounting top = topk.get( key );
				int i = 0;
				for( String val : top.keySet() ){
					statRow.append( val + " (" + top.getCount( val ) + ") <br/>\n" );
					i++;
					if( i > 10 )
						break;
				}
				statRow.append( "</td>" );
			}
		}
		
		header.append( "</tr>" );
		typeRow.append( "</tr>" );
		statRow.append( "</tr>" );
		
		p.println( header );
		p.println( typeRow );
		p.println( statRow );
		p.println( "</table>" );
	}
	
	
	public String toHtml(){
		StringBuffer s = new StringBuffer();
		s.append( "<table class=\"summary\">" );
		StringBuffer header = new StringBuffer( "<tr>" );
		StringBuffer typeRow = new StringBuffer( "<tr>" );
		StringBuffer statRow = new StringBuffer( "<tr>" );
		
		for( String key : types.keySet() ){
			header.append( "<th>" + key + "</th>" );
			typeRow.append( "<td>" + types.get( key ).getName() + "</td>" );
			
			if( types.get( key ) == Double.class ){
				statRow.append( "<td>" );
				statRow.append( "Minimum: " + minima.get(key ) + "<br/>" );
				statRow.append( "Maximum: " + maxima.get(key ) + "<br/>" );
				statRow.append( "</td>" );
			} else {
				statRow.append( "<td>" );
				SimpleTopKCounting top = topk.get( key );
				int i = 0;
				for( String val : top.keySet() ){
					statRow.append( val + " (" + top.getCount( val ) + ") <br/>\n" );
					i++;
					if( i > 10 )
						break;
				}
				statRow.append( "</td>" );
			}
		}
		
		header.append( "</tr>" );
		typeRow.append( "</tr>" );
		statRow.append( "</tr>" );
		
		s.append( header );
		s.append( typeRow );
		s.append( statRow );
		s.append( "</table>" );
		return s.toString();
	}
}