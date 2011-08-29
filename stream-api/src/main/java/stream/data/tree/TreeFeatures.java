package stream.data.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.TreeNode;

public class TreeFeatures implements DataProcessor {

	List<TreeFeature> features = new ArrayList<TreeFeature>();

	/**
	 * @see stream.data.DataProcessor#process(stream.data.Data)
	 */
	@Override
	public Data process(Data data) {
		
		List<String> treeKeys = new ArrayList<String>();
		
		for( String key : data.keySet() ){
			if( data.get( key ) instanceof TreeNode )
				treeKeys.add( key );
		}
		
		if( treeKeys.isEmpty() )
			return data;
		
		for( String treeKey : treeKeys )
			processTree( treeKey, data );
		
		return data;
	}
	
	
	public void add( TreeFeature feature ){
		features.add( feature );
	}
	
	
	public void processTree( String treeKey, Data data ){
		for( TreeFeature feat : features ){
			TreeNode tree = (TreeNode) data.get( treeKey );
			String featureName = feat.createFeatureKey( treeKey );
			Serializable featureValue = feat.compute( tree );
			data.put( featureName, featureValue );
		}
	}
}