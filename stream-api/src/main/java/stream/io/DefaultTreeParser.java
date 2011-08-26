package stream.io;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.DataUtils;
import stream.data.TreeNode;

public class DefaultTreeParser 
	implements Parser<TreeNode>, DataProcessor
{
	int pos = 0;
	String data = "";
	String sourceKey = "sql";
	
	
	
	public DefaultTreeParser(){
		this( "sql" );
	}
	
	
	public DefaultTreeParser( String sourceKey ){
		this.sourceKey = sourceKey;
	}
	
	
	
	/**
	 * @see stream.io.Parser#parse(java.lang.String)
	 */
	@Override
	public TreeNode parse(String input) throws Exception {
		data = input;
		pos = 0;
		return readTreeNode();
	}
	
	
	protected TreeNode readTreeNode() throws Exception {
		
		skip();
		read( "(" );
		String node = readToken( new char[]{ '(', ')' });
		skip();
		List<TreeNode> children = new ArrayList<TreeNode>();
		while( startsWith( "(" ) ){
			children.add( readTreeNode() );
			skip();
		}
		
		skip();
		read( ")" );
		return new DefaultTreeNode( node, null, children );
	}
	
	
	@Override
	public Data process(Data data) {
		
		if( sourceKey != null && data.get( sourceKey ) != null ){
			try {
				String source = data.get( sourceKey ).toString();
				TreeNode tree = parse( source );
				if( tree != null ){
					data.put( "@tree", tree );
				}
				
				DataUtils.hide( sourceKey, data );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return data;
	}

	
	protected String readToken() throws Exception {
		
		skip();
		StringBuffer tok = new StringBuffer();
		while( pos < data.length() && ! Character.isWhitespace( data.charAt( pos ) ) ){
			tok.append( data.charAt( pos++ ) );
		}
		
		return tok.toString();
	}
	
	
	protected String readToken( char[] delimiters ) throws Exception {
		skip();
		StringBuffer tok = new StringBuffer();
		while( pos < data.length() && ! contains( delimiters, data.charAt( pos ) ) ){
			tok.append( data.charAt( pos++ ) );
		}
		
		return tok.toString();
	}
	
	
	protected boolean contains( char[] set, char ch ){
		for( char c : set )
			if( c == ch )
				return true;
		return false;
	}
	
	
	protected boolean startsWith( String start ) {
		
		String remain = null;
		if( pos < data.length() )
			remain = data.substring( pos );
		
		return remain != null && remain.startsWith( start );
	}

	
	protected String read( String expected ) throws Exception {
		if( data.substring( pos ).startsWith( expected ) ){
			pos += expected.length();
			return expected;
		} else
			throw new Exception( "Could not read '" + expected + "' from string: " + data.substring( pos ) );
	}
	
	
	protected int skip(){
		int skipped = 0;
		while( pos < data.length() && Character.isWhitespace( data.charAt( pos ) ) ){
			pos++;
			skipped++;
		}
		return skipped;
	}
	
	
	public class DefaultTreeNode implements TreeNode {
		
		/** The unique class ID  */
		private static final long serialVersionUID = 5603730461142746019L;
		
		String label;
		TreeNode parent;
		Collection<TreeNode> children;
		
		
		public DefaultTreeNode( String label, TreeNode parent ){
			this( label, parent, new ArrayList<TreeNode>() );
		}
		
		
		public DefaultTreeNode( String label, TreeNode parent, Collection<TreeNode> siblings ){
			this.label = label;
			this.parent = parent;
			this.children = siblings;
		}
		
		
		@Override
		public TreeNode getParent() {
			return parent;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public void setLabel(String label) {
			this.label = label;
		}

		@Override
		public boolean isLeaf() {
			return children == null || children.isEmpty();
		}

		@Override
		public Collection<TreeNode> children() {
			return children;
		}

		@Override
		public void addChild(TreeNode node) {
			if( children == null )
				children = new ArrayList<TreeNode>();
			children.add( node );
		}
		
		
		public String toString(){
			StringBuffer s = new StringBuffer();
			s.append( "( " );
			s.append( label );
			for( TreeNode ch : children() ){
				s.append( " " );
				s.append( ch.toString() );
			}
			
			s.append( " )" );
			return s.toString();
		}
	}
	
	
	
	public static void main( String[] args ) throws Exception {
		
		String treeString = "( ROOT ( A1 ) ( A2 ) )";
		DefaultTreeParser parser = new DefaultTreeParser();
		TreeNode tree = parser.parse( treeString );
		
		System.out.println( "the tree is: " + tree );
	}
}