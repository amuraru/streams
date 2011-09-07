/**
 * 
 */
package stream.learner;

import java.io.Serializable;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.vector.SparseVector;
import stream.model.PredictionModel;

/**
 * <p>
 * This class implements an abstract classifier, i.e. an instance that is capable of learning
 * from observations of a specific, generic type <code>D</code> and predicting class values of
 * type <code>C</code>. 
 * </p>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 * @param <D> The data type of the input examples used for learning and prediction.
 * @param <C> The label type, i.e. the Java class of the predicted outcome.
 * 
 */
public abstract class AbstractClassifier<D,C> 
	implements Classifier<D,C>
{
	/** The unique class ID */
	private static final long serialVersionUID = -8809157061575037435L;

	static Logger log = LoggerFactory.getLogger( AbstractClassifier.class );
	

	/**
	 * @see stream.learner.Learner#getModel()
	 */
	@Override
	public final PredictionModel<D, C> getModel() {
		return this;
	}
	
	
	/**
	 * @see stream.learner.Learner#init()
	 */
	public void init(){
	}
	
	
	/**
	 * @see stream.model.PredictionModel#predict(java.lang.Object)
	 */
	@Override
	public abstract C predict(D item);
	

	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public abstract void learn(D item);
	
	

	public SparseVector createSparseVector( Data datum ){
		if( datum.containsKey( ".sparse-vector" ) ){
			log.trace( "Found existing sparse-vector object!" );
			return (SparseVector) datum.get( ".sparse-vector" );
		}
		
		for( Serializable val : datum.values() ){
			if( val instanceof SparseVector ){
				log.trace( "Found existing sparse-vector object!" );
				return (SparseVector) val;
			}
		}
		
		TreeSet<String> indexes = new TreeSet<String>();
		for( String key : LearnerUtils.getAttributes( datum ) ){
			Serializable val = datum.get( key );
			if( key.matches( "\\d+" ) && val instanceof Double ){
				log.debug( "Found numeric feature {}", key );
				indexes.add( key );
			} else {
				log.debug( "Skipping non-numeric feature {} of type {}", key, val.getClass() );
			}
		}
		
		double y = Double.NaN;
		if( datum.containsKey( "@label" ) ){
			try {
				y = (Double) datum.get( "@label" );
			} catch (Exception e) {
				y = Double.NaN;
			}
		}
		
		int[] idx = new int[ indexes.size() ];
		double[] vals = new double[ indexes.size() ];
		
		int i = 0;
		for( String key : indexes ){
			idx[i] = Integer.parseInt( key );
			vals[i] = (Double) datum.get( key );
			i++;
		}
		
		SparseVector vec = new SparseVector( idx, vals, y, false );
		log.trace( "SparseVector: {}", vec );
		return vec;
	}
}