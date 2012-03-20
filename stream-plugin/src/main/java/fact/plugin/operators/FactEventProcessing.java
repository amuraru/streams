/**
 * 
 */
package fact.plugin.operators;

import stream.data.Data;
import stream.plugin.processing.AbstractDataStreamProcess;

import com.rapidminer.operator.OperatorDescription;

import fact.plugin.FactEventObject;
import fact.plugin.FactEventStream;

/**
 * @author chris
 *
 */
public class FactEventProcessing 
	extends AbstractDataStreamProcess<FactEventStream,FactEventObject> {
	
	/**
	 * @param description
	 * @param title
	 * @param clazz
	 */
	public FactEventProcessing(OperatorDescription description ) {
		super(description, "Fact Event Processing", "fact event stream", FactEventStream.class, "evt fact event" );
	}

	/**
	 * @see stream.plugin.processing.AbstractDataStreamProcess#wrap(stream.data.Data)
	 */
	@Override
	public FactEventObject wrap(Data item) {
		return new FactEventObject( item );
	}
}