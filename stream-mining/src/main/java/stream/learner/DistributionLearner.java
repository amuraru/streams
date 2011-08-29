/**
 * 
 */
package stream.learner;

import java.util.HashMap;
import java.util.Map;

import stream.model.MultiDistributionModel;

/**
 * @author chris
 *
 */
public class DistributionLearner
implements Learner<Map<String,?>,MultiDistributionModel>
{
	/** The unique class ID */
	private static final long serialVersionUID = -6209280286985059891L;

	MultiDistributionModel model = null;
	Integer bins = 10;

	public void init( Map<String,String> params ) throws Exception {
		if( params.containsKey( "bins" ) )
			bins = new Integer( params.get( "bins" ) );
		
		model = new MultiDistributionModel( bins );
	}

	public void init(){
		try {
			init( new HashMap<String,String>() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see stream.learner.Learner#getModel()
	 */
	@Override
	public MultiDistributionModel getModel() {
		return model;
	}

	/**
	 * @see stream.learner.Learner#learn(java.lang.Object)
	 */
	@Override
	public void learn(Map<String, ?> item) {
		model.update( item );
	}
}