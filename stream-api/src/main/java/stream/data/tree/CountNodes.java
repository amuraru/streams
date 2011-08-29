package stream.data.tree;

import java.io.Serializable;

import stream.data.TreeNode;

public class CountNodes 
	implements TreeFeature 
{
	/**
	 * @see stream.data.tree.TreeFeature#createFeatureKey(java.lang.String)
	 */
	@Override
	public String createFeatureKey(String inputKey) {
		return "nodeCount(" + inputKey + ")";
	}

	@Override
	public Serializable compute(TreeNode tree) {
		return getNumberOfNodes( tree );
	}
	
	
	public Integer getNumberOfNodes( TreeNode tree ){
		if( tree.isLeaf() )
			return 1;
		
		Integer sum = 1;
		for( TreeNode ch : tree.children() )
			sum += getNumberOfNodes( ch );
				
		return sum;
	}
}