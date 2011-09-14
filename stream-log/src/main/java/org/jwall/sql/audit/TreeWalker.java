package org.jwall.sql.audit;

import java.util.Stack;

import stream.data.TreeNode;

public abstract class TreeWalker
{
    TreeNode root;
    Stack<TreeNode> stack = new Stack<TreeNode>();
    
    
    public TreeNode current(){
        if( stack.isEmpty() ){
            root = new ASTNode( "root" );
            stack.push( root );
        }
        return stack.peek();
    }
    
    
    protected TreeNode dive( String key ){
        TreeNode node = new ASTNode( encode(key) );
        stack.push( node );
        return node;
    }
    
    
    protected void up(){
        stack.pop();
    }

    
    protected TreeNode leaf( Object o ){
        TreeNode leaf = new ASTNode( encode(o.toString()) );
        current().addChild( leaf );
        return leaf;
    }
    
    protected String encode( String str ){
        return str.replaceAll( "\\(", "\\\\(" ).replaceAll( "\\)", "\\\\)" );
    }
    
    
    public abstract TreeNode create( String label );
}
