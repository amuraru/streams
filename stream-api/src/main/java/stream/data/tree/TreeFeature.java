package stream.data.tree;

import java.io.Serializable;

import stream.data.TreeNode;

public interface TreeFeature {

	public String createFeatureKey( String inputKey );
	
	public Serializable compute( TreeNode tree );
}
