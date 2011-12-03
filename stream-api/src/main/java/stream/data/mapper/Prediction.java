/**
 * 
 */
package stream.data.mapper;

import java.io.Serializable;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.mining.PredictionModel;

/**
 * @author chris
 *
 */
public class Prediction implements DataProcessor {

	PredictionModel<?> model;
	
	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		if( model != null ){
			Serializable prediction = model.predict( data );
			data.put( "@pred:" + model.getClass().getName(), prediction );
		}
		
		return data;
	}
	
	
	public void setModel( PredictionModel<?> model ){
		this.model = model;
	}
	
	public PredictionModel<?> getModel(){
		return this.model;
	}
}