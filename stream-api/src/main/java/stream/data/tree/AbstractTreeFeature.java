package stream.data.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.TreeNode;

public abstract class AbstractTreeFeature
    implements TreeFeature, DataProcessor
{
    String id;
    String missingValue = "null";

    /**
     * @see stream.data.DataProcessor#process(stream.data.Data)
     */
    @Override
    public Data process(Data data) {

        List<String> treeKeys = new ArrayList<String>();

        for( String key : data.keySet() ){
            if( data.get( key ) instanceof TreeNode )
                treeKeys.add( key );
        }

        if( treeKeys.isEmpty() )
            return data;

        for( String treeKey : treeKeys ){
            processTree( treeKey, data );
        }

        return data;
    }


    public void processTree( String treeKey, Data data ){
        TreeNode tree = (TreeNode) data.get( treeKey );
        String featureName = this.createFeatureKey( treeKey );
        Serializable featureValue = this.compute( tree );
        data.put( featureName, featureValue );
    }
    
    

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }


    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }


    /**
     * @return the missingValue
     */
    public String getMissing()
    {
        return missingValue;
    }


    /**
     * @param missingValue the missingValue to set
     */
    public void setMissing(String missingValue)
    {
        this.missingValue = missingValue;
    }


    @Override
    public String createFeatureKey(String inputKey){
        return getId() + "(" + inputKey + ")";
    }

    @Override
    public abstract Serializable compute(TreeNode tree);
}
