package org.jwall.sql.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import stream.data.TreeNode;





/**
 * <p>
 * This implementation represents a tree node within the parse tree of an SQL query. 
 * </p>
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 */
public class ASTNode
	//extends NLPTreeNode
	implements TreeNode, Serializable
{
	/** The unique class ID */
    private static final long serialVersionUID = 7492136318817801260L;
    String nodeName = "";
	String label;
	ASTNode parent;
	
	final List<TreeNode> children = new ArrayList<TreeNode>();
	
	public ASTNode( String nodeName ){
		this.nodeName = nodeName;
		this.label = nodeName;
	}
	
	
	public TreeNode getParent(){
		return parent;
	}
	

	public int getDepth(){
	    if( parent == null )
	        return 0;
	    else
	        return 1 + parent.getDepth();
	}



	/**
	 * @see org.jwall.sql.parser.TreeNode#getLabel()
	 */
	@Override
	public String getLabel() {
		return label;
	}
	
	/**
	 * @see org.jwall.sql.parser.TreeNode#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @see org.jwall.sql.parser.TreeNode#children()
	 */
	@Override
	public Collection<TreeNode> children() {
		return children;
	}

	/**
	 * @see org.jwall.sql.parser.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return children().isEmpty();
	}




	@Override
	public void addChild(TreeNode node) {
		children.add( node );
	}
	
	
	public String toString(){
	    return toString( this );
	}
	

    public static String toString( TreeNode node ){
        StringBuffer s = new StringBuffer();
        s.append( "( " );
        if( node.getLabel() == null || "".equals( node.getLabel() ) )
            s.append( "{}" );
        else
            s.append( node.getLabel() );
        s.append( " " );
        for( TreeNode child : node.children() ){
            //if( i++ == 0 )
            //    s.append( " " );
            s.append( toString( child ) );
            s.append( " " );
        }
        s.append( ")" );
        return s.toString();
    }
}