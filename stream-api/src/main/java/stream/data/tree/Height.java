package stream.data.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import stream.data.TreeNode;

public class Height 
    extends AbstractTreeFeature
{
	/**
	 * @see stream.data.tree.TreeFeature#createFeatureKey(java.lang.String)
	 */
	@Override
	public String createFeatureKey(String inputKey) {
		return "height(" + inputKey + ")";
	}

	@Override
	public Serializable compute(TreeNode tree) {
		return getHeight( tree );
	}
	
	
	
	public Integer getHeight( TreeNode tree ){
		if( tree.isLeaf() )
			return 0;
		
		List<Integer> list = new ArrayList<Integer>();
		for( TreeNode ch : tree.children() )
			list.add( getHeight( ch ) );
				
		return 1 + max( list );
	}
	
	
	public Integer getNumberOfNodes( TreeNode tree ){
		if( tree.isLeaf() )
			return 1;
		
		Integer sum = 1;
		for( TreeNode ch : tree.children() )
			sum += getNumberOfNodes( ch );
				
		return sum;
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