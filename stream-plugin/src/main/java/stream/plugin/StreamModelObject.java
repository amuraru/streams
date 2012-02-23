/**
 * 
 */
package stream.plugin;

import java.io.Serializable;

import stream.data.Data;
import stream.model.PredictionModel;

import com.rapidminer.operator.AbstractIOObject;
import com.rapidminer.operator.Annotations;

/**
 * @author chris
 *
 */
public class StreamModelObject extends AbstractIOObject {

	/** The unique class ID */
	private static final long serialVersionUID = 7903408876270725612L;

	PredictionModel<Data,Serializable> model;
	
	
	public StreamModelObject(){
	}

	public StreamModelObject( PredictionModel<Data,Serializable> model ){
		this.model = model;
	}
	
	/**
	 * @see com.rapidminer.operator.IOObject#getAnnotations()
	 */
	@Override
	public Annotations getAnnotations() {
		Annotations annotations = new Annotations();
		return annotations;
	}
	
	public PredictionModel<Data,Serializable> getPredictionModel(){
		return model;
	}
	
	public void apply( Data item ){
		Serializable prediction = model.predict( item );
		item.put( "@prediction", prediction );
	}
}