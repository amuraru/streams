package stream.data.tree;

import java.io.Serializable;
import java.util.Collection;

import stream.data.TreeNode;

public class CountLeaves 
	implements TreeFeature 
{
	/**
	 * @see stream.data.tree.TreeFeature#createFeatureKey(java.lang.String)
	 */
	@Override
	public String createFeatureKey(String inputKey) {
		return "leafCount(" + inputKey + ")";
	}

	@Override
	public Serializable compute(TreeNode tree) {
		return getNumberOfLeaves( tree );
	}
	
	public Integer getNumberOfLeaves( TreeNode tree ){
		if( tree.isLeaf() )
			return 1;
		
		Integer sum = 0;
		for( TreeNode ch : tree.children() )
			sum += getNumberOfLeaves( ch );
				
		return sum;
	}
	
	
	protected Integer max( Collection<Integer> input ){
		Integer max = null;
		for( Integer i : input ){
			if( max == null || i > max )
				max = i;
		}
		return max;
	}
}