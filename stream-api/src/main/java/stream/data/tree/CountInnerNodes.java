package stream.data.tree;

import stream.data.TreeNode;

public class CountInnerNodes 
	extends CountNodes
{
    
    public CountInnerNodes(){
        id = "innerNodeCount";
    }
    
	public Integer getNumberOfNodes( TreeNode tree ){
		if( tree.isLeaf() )
			return 0;
		
		Integer sum = 1;
		for( TreeNode ch : tree.children() )
			sum += getNumberOfNodes( ch );
				
		return sum;
	}
}