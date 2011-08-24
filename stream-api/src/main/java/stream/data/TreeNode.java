package stream.data;

import java.io.Serializable;
import java.util.Collection;


/**
 * This interface defines a simple tree node.
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public interface TreeNode
	extends Serializable
{
	public TreeNode getParent();
	
	public String getLabel();
	
	public void setLabel( String label );
	
	public boolean isLeaf();
	
	public Collection<TreeNode> children();
	
	public void addChild( TreeNode node );
}