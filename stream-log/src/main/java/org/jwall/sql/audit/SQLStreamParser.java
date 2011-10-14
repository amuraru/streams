package org.jwall.sql.audit;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import org.jwall.web.audit.io.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stream.data.Data;
import stream.data.DataProcessor;
import stream.data.TreeNode;
import stream.data.mapper.Mapper;


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
    
    Map<String,String> fixes = new HashMap<String,String>();
    
    /* The name of the feature containing the SQL query */
    String key = "sql";

    Long success = 0L;
    Long error = 0L;
    
    PrintStream errorStream;
    List<Mapper<String,String>> preprocessor = new ArrayList<Mapper<String,String>>(); 
    
    public SQLStreamParser(){
        this( "sql" );
    }
    
    
    public SQLStreamParser( String key ){
        setKey( key );
    }
    
    public Long getErrorCount(){
        return error;
    }
    
    public Long getSuccessCount(){
        return success;
    }
    
    public Long getTotalCount(){
        return error + success;
    }
    
    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    public void addMapper( Mapper<String,String> mapper ){
        this.preprocessor.add( mapper );
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
        
        if( !data.containsKey( key ) || "null".equals( data.get( key ) + "" ) ){
            //log.error( "Input does not contain data for key '{}'", key );
            log.debug( "Skipping item {}", data );
            return data;
        }
        
        String query = data.get( key ).toString();
        
        for( Mapper<String,String> mapper : preprocessor ){
            String orig = query;
            try {
                query = mapper.map( query );
            } catch (Exception e) {
                if( log.isTraceEnabled() )
                    e.printStackTrace();
                query = orig;
            }
        }
        
        try {
            
            TreeNode tree = parse( query );
            if( tree != null ){
                String outkey = "@tree:" + key;
                log.debug( "Storing tree as feature '{}': {}", outkey, tree );
                data.put( outkey, tree );
                success++;
            }
        } catch (Exception e) {
            
            if( errorStream != null )
                errorStream.println( query );
            
            log.error( "Failed to parse SQL: '{}'", query );
            log.error( "  Error was: {}", e.getMessage() );
            if( log.isTraceEnabled() )
                e.printStackTrace();
            error++;
        }
        
        return data;
    }
    
    
    
    
    /**
     * @return the errorStream
     */
    public PrintStream getErrorStream()
    {
        return errorStream;
    }


    /**
     * @param errorStream the errorStream to set
     */
    public void setErrorStream(OutputStream errorStream)
    {
        this.errorStream = new PrintStream( errorStream );
    }


    public TreeNode parse( String statement ) throws ParseException {
        log.trace( "Need to parse SQL query: '{}'", statement );
        
        String sql = statement;
        for( String fix : fixes.keySet() ){
            if( sql.indexOf( fix ) > 0 )
                sql = sql.replace( fix, fixes.get( fix ) );
        }
        
        try {
            
            Statement stmt = pm.parse( new StringReader( sql ) );
            log.trace( "Statement is: {}", stmt );
            
            JSqlASTWalker astWalker = new JSqlASTWalker();
            stmt.accept( astWalker );
            return astWalker.getAST();
            
        } catch (Exception e) {
            if( log.isTraceEnabled() )
                e.printStackTrace();
            throw new ParseException( "Failed to parse sql: '" + sql + "'" );
        }
    }
    
    public Map<String,String> getFixes(){
        return fixes;
    }
}