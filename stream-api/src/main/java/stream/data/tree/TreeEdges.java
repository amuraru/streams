package stream.data.tree;

import java.util.LinkedHashMap;
import java.util.Map;

import stream.data.Data;
import stream.data.TreeNode;

public class TreeEdges extends TreeFeatures {

	/**
	 * @see stream.data.tree.TreeFeatures#processTree(java.lang.String, stream.data.Data)
	 */
	@Override
	public void processTree(String treeKey, Data data) {
		
		TreeNode tree = (TreeNode) data.get( treeKey );
		
		Map<String,Integer> edges = getEdges( tree );
		for( String key : edges.keySet() ){
			
			String fk = treeKey; // feature key => if treeKey is an annotation, we need to remove the leading '@'
			if( fk.startsWith( "@" ) )
				fk = fk.substring( 1 );
			
			data.put( fk + ":edge[" + key + "]", edges.get( key ) );
		}
	}
	
	
	public Map<String,Integer> getEdges( TreeNode node ){
		
		Map<String,Integer> edges = new LinkedHashMap<String,Integer>();
		
		for( TreeNode ch : node.children() ){
			
			String edge = node.getLabel().trim() + "->" + ch.getLabel().trim();
			Integer count = edges.get( edge );
			if( count == null ){
				count = 1;
			} else
				count += 1;
			
			edges.put( edge, count );
			
			edges = add( edges, getEdges( ch ) );
		}
		
		return edges;
	}
	
	
	public Map<String,Integer> add( Map<String,Integer> m1, Map<String,Integer> m2 ){
		
		for( String key : m2.keySet() ){
			if( m1.containsKey( key ) )
				m1.put( key, m1.get( key ) + m2.get( key ) );
			else
				m1.put( key, m2.get( key ) );
		}
		
		return m1;
	}
}