/**
 * 
 */
package stream.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.learner.LearnerUtils;

/**
 * <p>
 * This model does not do a prediction, but is used to extract the
 * ground truth from labeled data. It allows for deployment of this
 * model as baseline within a test-then-train evaluation.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class TrueLabelModel<R> implements PredictionModel<Data, R> {

	/** The unique class ID */
	private static final long serialVersionUID = -530566641499137409L;
	
	/* The global logger for this class */
	static Logger log = LoggerFactory.getLogger( TrueLabelModel.class );
	
	/* The label attribute that is being used */
	String labelAttribute = null;
	
	
	/**
	 * Creates a new model without a pre-set label name. The label is then
	 * auto-detected.
	 */
	public TrueLabelModel(){
	}

	
	public void init(){
	}
	
	
	/**
	 * Creates a new model which will use the given label name. 
	 * 
	 * @param labelName
	 */
	public TrueLabelModel( String labelName ){
		setLabelAttribute( labelName );
	}
	
	
	/**
	 * @return the labelAttribute
	 */
	public String getLabelAttribute() {
		return labelAttribute;
	}


	/**
	 * @param labelAttribute the labelAttribute to set
	 */
	public void setLabelAttribute(String labelAttribute) {
		this.labelAttribute = labelAttribute;
	}


	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public R predict(Data item) {
		
		if( labelAttribute == null ){
			labelAttribute = LearnerUtils.detectLabelAttribute( item );
			if( labelAttribute == null )
				return null;
			
			/*
			for( String attribute : item.keySet() ){
				if( attribute.startsWith( "_class" ) ){
					labelAttribute = attribute;
					break;
				} else
					labelAttribute = attribute;
			}
			 */
			log.info( "Auto-detection of class attribute returned attribute: '{}'", labelAttribute );
		}
		
		return (R) item.get( labelAttribute );
	}
}