/**
 * 
 */
package com.rapidminer.stream;

import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.OutputPort;

/**
 * @author chris
 *
 */
public class DataStreamLearner extends DataStreamOperator {

	OutputPort model = getOutputPorts().createPort( "model" );
	
	/**
	 * @param description
	 * @param clazz
	 */
	public DataStreamLearner(OperatorDescription description, Class<?> clazz) {
		super(description, clazz);
	}
}