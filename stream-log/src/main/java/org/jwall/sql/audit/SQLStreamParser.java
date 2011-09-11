package org.jwall.sql.audit;

import java.io.StringReader;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import org.jwall.log.JSqlASTWalker;
import org.jwall.sql.parser.TreePrinter;
import org.jwall.web.audit.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.TreeNode;


/**
 * This class implements a data processor for enriching data items with a
 * parse tree of any SQL query that might be provided as a feature of the
 * item. 
 * 
 * @author Christian Bockermann &lt;chris@jwall.org&gt;
 *
 */
public class SQLStreamParser
    implements DataProcessor
{
    /* The global logger for this class */
    static Logger log = LoggerFactory.getLogger( SQLStreamParser.class );
    
    final CCJSqlParserManager pm = new CCJSqlParserManager();
    
    /* The name of the feature containing the SQL query */
    String key = "sql";


    
    
    public SQLStreamParser(){
        this( "sql" );
    }
    
    
    public SQLStreamParser( String key ){
        setKey( key );
    }
    
    
    
    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }


    /**
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }


    /**
     * @see stream.data.DataProcessor#process(stream.data.Data)
     */
    @Override
    public Data process(Data data)
    {
        if( key == null ){
            log.error( "No key specified to parse sql data from!" );
            return data;
        }
        
        if( !data.containsKey( key ) ){
            log.error( "Input does not contain data for key '{}'", key );
            return data;
        }
        
        String query = data.get( key ).toString();
        
        try {
            
            TreeNode tree = parse( query );
            if( tree != null ){
                String outkey = "@tree:" + key;
                log.info( "Storing tree as feature '{}': {}", outkey, TreePrinter.toString( tree ) );
                data.put( outkey, tree );
            }
            
        } catch (Exception e) {
            log.error( "Failed to parse SQL: '{}'", query );
            log.error( "  Error was: {}", e.getMessage() );
            if( log.isDebugEnabled() )
                e.printStackTrace();
        }
        
        return data;
    }
    
    
    public TreeNode parse( String sql ) throws ParseException {
        log.info( "Need to parse SQL query: '{}'", sql );
        
        try {
            
            Statement stmt = pm.parse( new StringReader( sql ) );
            log.info( "Statement is: {}", stmt );
            
            JSqlASTWalker astWalker = new JSqlASTWalker();
            stmt.accept( astWalker );
            return astWalker.getAST();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}