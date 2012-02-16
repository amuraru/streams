/**
 * 
 */
package stream.data;

import stream.util.Description;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Christian Bockermann &lt;christian.bockermann@udo.edu&gt;
 *
 */
@Description( group="stream.data.test" )
public class PrintDataProcessor implements DataProcessor {

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		if( data == null )
			return null;
		
		System.out.println( "data-item: " + data );
		return data;
	}
}