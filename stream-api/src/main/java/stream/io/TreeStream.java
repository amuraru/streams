package stream.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import stream.data.Data;
import stream.data.DataImpl;
import stream.data.TreeNode;


/**
 * A tree stream is a simple entity that reads and parses trees, one tree per line.
 * The trees are expected to be in the default NLP format:
 * <pre>
 *    ( ROOT ( A1 ( A1.1 ) ( A1.2 ) ) ( A2 ) )
 * </pre>
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class TreeStream implements DataStream {

	String treeAttribute = "tree";
	BufferedReader reader;
	DefaultTreeParser treeParser;
	Map<String, Class<?>> attributes = new LinkedHashMap<String,Class<?>>();
	
	public TreeStream( URL url ) throws Exception {
		reader = new BufferedReader( new InputStreamReader( url.openStream() ) );
		treeParser = new DefaultTreeParser();
		attributes.put( "tree", TreeNode.class );
	}
	
	
	public String getTreeAttribute() {
		return treeAttribute;
	}


	public void setTreeAttribute(String treeAttribute) {
		this.treeAttribute = treeAttribute;
	}


	/**
	 * @see stream.io.DataStream#getAttributes()
	 */
	@Override
	public Map<String, Class<?>> getAttributes() {
		return attributes;
	}

	
	/**
	 * @see stream.io.DataStream#readNext()
	 */
	@Override
	public Data readNext() throws Exception {
		return readNext( new DataImpl() );
	}

	
	/**
	 * @see stream.io.DataStream#readNext(stream.data.Data)
	 */
	@Override
	public Data readNext(Data datum) throws Exception {
		String line = reader.readLine();
		
		// skip comment lines
		//
		while( line != null && line.startsWith( "#" ) )
			line = reader.readLine();
		
		if( line == null )
			return null;
		
		TreeNode tree = treeParser.parse( line );
		
		datum.put( treeAttribute, tree );
		return datum;
	}
}