/**
 * 
 */
package stream.learner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import stream.data.Data;

/**
 * <p>
 * This learner does not learn anything and is used to simply return the label
 * of labeled data. It is a useful learner as baseline to evaluate different
 * learning algorithms against the true label.
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class MultiLabelPredictor<T> extends AbstractClassifier<Data,Map<String,T>> {

	/** The unique class ID */
	private static final long serialVersionUID = -67867885352106994L;

	List<String> attributes = new ArrayList<String>();
	
	/**
	 * @return the labelAttribute
	 */
	public String getLabelAttributes() {
		return join( attributes, "," );
	}

	
	/**
	 * @param labelAttribute the labelAttribute to set
	 */
	public void setLabelAttributes(String labelAttribute) {
		attributes = new ArrayList<String>();
		if( labelAttribute.indexOf( "," ) > 0 ){
			for( String attr : labelAttribute.split( "," ) )
				attributes.add( attr.trim() );
		} else
			attributes.add( labelAttribute );
	}


	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn(Data item) {
		if( attributes.isEmpty() )
			attributes.addAll( item.keySet() );
	}


	/**
	 * @see stream.learner.AbstractClassifier#predict(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, T> predict(Data item) {
		Map<String,T> predictions = new LinkedHashMap<String,T>();
		
		for( String att : item.keySet() )
			predictions.put( att, (T) item.get( att ) );
		
		return predictions;
	}
	
	
	public String join( List<String> str, String glue ){
		StringBuffer s = new StringBuffer();
		Iterator<String> it = str.iterator();
		while( it.hasNext() ){
			s.append( it.next() );
			if( it.hasNext() )
				s.append( glue );
		}
		return s.toString();
	}
}